package com.pezont.teammates.data.api

import com.pezont.teammates.data.dto.LoginRequestDto
import com.pezont.teammates.data.dto.LoginResponseDto
import com.pezont.teammates.data.dto.UpdateTokenRequestDto
import com.pezont.teammates.data.dto.UpdateTokenResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface TeammatesAuthApiService {

        @POST("login")
        suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

        @POST("update_tokens")
        suspend fun updateTokens(@Body request: UpdateTokenRequestDto): UpdateTokenResponseDto

}