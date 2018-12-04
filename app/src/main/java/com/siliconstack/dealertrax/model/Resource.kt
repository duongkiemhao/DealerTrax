package com.siliconstack.dealertrax.model

import com.bol.instantapp.exception.AppException
import com.google.gson.annotations.SerializedName


class Resource<T> private constructor(val status: Resource.Status, val data: T?, val exception: AppException?) {
    enum class Status {
        SUCCESS, ERROR,INFO,UNAUTHORIZED
    }

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(exception: AppException?): Resource<T> {
            return Resource(Status.ERROR, null, exception)
        }
        fun <T> info(data: T?): Resource<T>? {
            return Resource(Status.INFO, data, null)
        }
        fun <T> unauthorized(data: T?): Resource<T>? {
            return Resource(Status.UNAUTHORIZED, data, null)
        }

    }
}

data class ErrorResponse (
        @SerializedName("Message")
        var message: String? =null
)


interface BaseApiResponse {

}

