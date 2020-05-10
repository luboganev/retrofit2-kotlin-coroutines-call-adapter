package com.melegy.retrofitcoroutines.remote

import java.io.IOException

/**
 * A wrapper around all possible results a remote API call could produce.
 *
 * - [Success] Success with non-empty body
 * - [SuccessEmpty] Success with empty body
 * - [Error] Error with specific non-empty error body
 * - [ErrorEmpty] Error with empty body
 * - [NetworkFailure] Failure due to network issues
 * - [UnknownFailure] Unexpected failure
 */
sealed class NetworkResponse<out S : Any, out E : Any> {
    /**
     * Success response with body.
     */
    data class Success<S : Any>(val code: Int, val body: S) : NetworkResponse<S, Nothing>()

    /**
     * Success response with empty body. For API requests whose body is empty or not interesting use
     * [Void] for the body type. For those cases this value shall be returned.
     */
    data class SuccessEmpty(val code: Int) : NetworkResponse<Nothing, Nothing>()

    /**
     * Error response with error body
     */
    data class Error<E : Any>(val code: Int, val body: E) : NetworkResponse<Nothing, E>()

    /**
     * Error response with empty body. For API requests whose error body is empty or not interesting
     * use [Void] for the error body type. For those cases this value shall be returned.
     */
    data class ErrorEmpty(val code: Int) : NetworkResponse<Nothing, Nothing>()

    /**
     * Network failure
     */
    data class NetworkFailure(val error: IOException) : NetworkResponse<Nothing, Nothing>()

    /**
     * For example, json parsing error
     */
    data class UnknownFailure(val error: Throwable?) : NetworkResponse<Nothing, Nothing>()
}
