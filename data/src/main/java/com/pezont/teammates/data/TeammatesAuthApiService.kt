package com.pezont.teammates.data

import com.pezont.teammates.domain.model.LoginRequest
import com.pezont.teammates.domain.model.LoginResponse
import com.pezont.teammates.domain.model.UpdateTokenRequest
import com.pezont.teammates.domain.model.UpdateTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TeammatesAuthApiService {


        @POST("login")
        suspend fun login(@Body request: LoginRequest): LoginResponse

        @POST("update_tokens")
        suspend fun updateTokens(@Body request: UpdateTokenRequest): UpdateTokenResponse
}