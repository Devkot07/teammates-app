package com.pezont.teammates.domain.model

data class User(
    val nickname: String = "",
    val publicId: String = "",
    val email: String = "",
    val description: String = "",
    val imagePath: String = "",
)
