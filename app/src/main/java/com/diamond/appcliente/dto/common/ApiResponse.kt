package com.diamond.appcliente.dto.common

class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T?
) {
    companion object {
        fun <T> success(message: String, data: T?) = ApiResponse("success", message, data)
        fun <T> error(message: String, data: T?) = ApiResponse("error", message, data)
    }
}
