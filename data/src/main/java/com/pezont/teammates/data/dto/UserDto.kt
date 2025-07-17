package com.pezont.teammates.data.dto

import com.google.gson.annotations.SerializedName
import com.pezont.teammates.domain.model.User

data class UserDto(
    val nickname: String? = null,
    @SerializedName("public_id", alternate = ["id"])
    val publicId: String? = null,
    val email: String? = null,
    val description: String? = null,
    @SerializedName("image_path")
    val imagePath: String? = null,
) {
    fun toDomain(): User = User(
        publicId = this.publicId.orEmpty(),
        nickname = this.nickname.orEmpty(),
        email = this.email.orEmpty(),
        description = this.description.orEmpty(),
        imagePath = this.imagePath.orEmpty()
    )
}