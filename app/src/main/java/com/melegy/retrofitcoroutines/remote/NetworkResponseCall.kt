package com.melegy.retrofitcoroutines.remote

import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

internal class NetworkResponseCall<S : Any, E : Any>(
    private val delegate: Call<S>,
    private val errorConverter: Converter<ResponseBody, E>?
) : Call<NetworkResponse<S, E>> {

    override fun enqueue(callback: Callback<NetworkResponse<S, E>>) {
        return delegate.enqueue(object : Callback<S> {
            override fun onResponse(call: Call<S>, response: Response<S>) {
                callback.onResponse(
                    this@NetworkResponseCall,
                    Response.success(convertSuccessToNetworkResponse(response))
                )
            }

            override fun onFailure(call: Call<S>, throwable: Throwable) {
                callback.onResponse(
                    this@NetworkResponseCall, Response.success(
                        convertFailureToNetworkResponse(throwable)
                    )
                )
            }
        })
    }

    override fun isExecuted() = delegate.isExecuted

    override fun clone() = NetworkResponseCall(delegate.clone(), errorConverter)

    override fun isCanceled() = delegate.isCanceled

    override fun cancel() = delegate.cancel()

    override fun execute(): Response<NetworkResponse<S, E>> {
        val response: Response<S>
        try {
            response = delegate.execute()
        } catch (ex: Exception) {
            return Response.success(convertFailureToNetworkResponse(ex))
        }
        return Response.success(convertSuccessToNetworkResponse(response))
    }

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    private fun convertSuccessToNetworkResponse(response: Response<S>): NetworkResponse<S, E> {
        val body = response.body()
        val code = response.code()
        val error = response.errorBody()

        if (response.isSuccessful) {
            return if (body != null) {
                NetworkResponse.Success(code, body)
            } else {
                NetworkResponse.SuccessEmpty(code)
            }
        } else {
            if (errorConverter != null) {
                val errorBody: E = try {
                    requireNotNull(error) {
                        "Unexpected null error body"
                    }
                    require(error.contentLength() != 0L) {
                        "Unexpected empty error body"
                    }
                    val parsedErrorBody = errorConverter.convert(error)
                    requireNotNull(parsedErrorBody) {
                        "Unexpected null parsed error"
                    }
                    parsedErrorBody
                } catch (ex: Exception) {
                    return NetworkResponse.UnknownFailure(ex)
                }

                return NetworkResponse.Error(code, errorBody)
            } else {
                return NetworkResponse.ErrorEmpty(code)
            }
        }
    }

    private fun convertFailureToNetworkResponse(throwable: Throwable): NetworkResponse<S, E> =
        when (throwable) {
            is IOException -> NetworkResponse.NetworkFailure(throwable)
            else -> NetworkResponse.UnknownFailure(throwable)
        }
}
