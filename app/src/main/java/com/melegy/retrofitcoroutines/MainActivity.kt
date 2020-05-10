package com.melegy.retrofitcoroutines

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.melegy.retrofitcoroutines.remote.NetworkResponse
import com.melegy.retrofitcoroutines.remote.NetworkResponseAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val moshi = Moshi.Builder().build()
        val okHttpClient = OkHttpClient.Builder().build()
        val retrofit = createRetrofit(moshi, okHttpClient)
        val service = retrofit.create<ApiService>()

        GlobalScope.launch {
            when (val response1 = service.getSuccess()) {
                is NetworkResponse.Success -> Log.d(TAG, "Success[${response1.code}]\n${response1.body}")
                is NetworkResponse.SuccessEmpty -> Log.d(TAG, "SuccessEmpty[${response1.code}]")
                is NetworkResponse.Error -> Log.d(TAG, "Error[${response1.code}]\n${response1.body}")
                is NetworkResponse.ErrorEmpty -> Log.d(TAG, "ErrorEmpty[${response1.code}]")
                is NetworkResponse.NetworkFailure -> Log.d(TAG, "NetworkError")
                is NetworkResponse.UnknownFailure -> Log.d(TAG, "UnknownError: ${response1.error}")
            }

            when (val response2 = service.getError()) {
                is NetworkResponse.Success -> Log.d(TAG, "Success[${response2.code}]\n${response2.body}")
                is NetworkResponse.SuccessEmpty -> Log.d(TAG, "SuccessEmpty[${response2.code}]")
                is NetworkResponse.Error -> Log.d(TAG, "Error[${response2.code}]\n${response2.body}")
                is NetworkResponse.ErrorEmpty -> Log.d(TAG, "ErrorEmpty[${response2.code}]")
                is NetworkResponse.NetworkFailure -> Log.d(TAG, "NetworkError")
                is NetworkResponse.UnknownFailure -> Log.d(TAG, "UnknownError: ${response2.error}")
            }

            when (val response3 = service.getSuccessEmpty()) {
                is NetworkResponse.Success -> Log.d(TAG, "Success[${response3.code}]\n${response3.body}")
                is NetworkResponse.SuccessEmpty -> Log.d(TAG, "SuccessEmpty[${response3.code}]")
                is NetworkResponse.Error -> Log.d(TAG, "Error[${response3.code}]\n${response3.body}")
                is NetworkResponse.ErrorEmpty -> Log.d(TAG, "ErrorEmpty[${response3.code}]")
                is NetworkResponse.NetworkFailure -> Log.d(TAG, "NetworkError")
                is NetworkResponse.UnknownFailure -> Log.d(TAG, "UnknownError: ${response3.error}")
            }

            when (val response4 = service.getErrorEmpty()) {
                is NetworkResponse.Success -> Log.d(TAG, "Success[${response4.code}]\n${response4.body}")
                is NetworkResponse.SuccessEmpty -> Log.d(TAG, "SuccessEmpty[${response4.code}]")
                is NetworkResponse.Error -> Log.d(TAG, "Error[${response4.code}]\n${response4.body}")
                is NetworkResponse.ErrorEmpty -> Log.d(TAG, "ErrorEmpty[${response4.code}]")
                is NetworkResponse.NetworkFailure -> Log.d(TAG, "NetworkError")
                is NetworkResponse.UnknownFailure -> Log.d(TAG, "UnknownError: ${response4.error}")
            }

        }
    }

    interface ApiService {
        @GET("success")
        suspend fun getSuccess(): NetworkResponse<Success, Error>

        @GET("success")
        suspend fun getSuccessEmpty(): NetworkResponse<Void, Error>

        @GET("error")
        suspend fun getError(): NetworkResponse<Success, Error>

        @GET("error")
        suspend fun getErrorEmpty(): NetworkResponse<Success, Void>
    }

    private fun createRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://retroftcoroutines.free.beeceptor.com/")
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
