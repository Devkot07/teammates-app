package com.pezont.teammates.models

data class Credentials(
    var login: String = "",
    var pwd: String = "",
) {
    fun isNotEmpty(): Boolean {
        return login.isNotEmpty() && pwd.isNotEmpty()
    }
}