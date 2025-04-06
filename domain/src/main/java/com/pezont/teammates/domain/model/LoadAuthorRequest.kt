package com.pezont.teammates.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class LoadAuthorRequest(
    @SerializedName("nickname")
    val nickname: String? = null,
    @SerializedName("public_id")
    val authorId: String? = null,
)