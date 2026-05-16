package com.diamond.appcliente.dto.login

data class LoginResponse(
    var status: String? = null,
    var message: String? = null,
    var data: LoginData? = null
) {
    data class LoginData(
        var token: String? = null,
        var refreshToken: String? = null
    )
}
