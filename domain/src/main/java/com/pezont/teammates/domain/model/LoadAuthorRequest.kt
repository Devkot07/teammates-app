package com.pezont.teammates.domain.model

import com.google.gson.annotations.SerializedName


data class LoadAuthorRequest(
    @SerializedName("nickname")
    val nickname: String? = null,
    @SerializedName("public_id")
    val authorId: String? = null,
)