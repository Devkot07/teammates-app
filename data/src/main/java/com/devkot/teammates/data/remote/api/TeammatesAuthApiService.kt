package com.devkot.teammates.data.remote.api

import com.devkot.teammates.data.remote.dto.LoginRequestDto
import com.devkot.teammates.data.remote.dto.LoginResponseDto
import com.devkot.teammates.data.remote.dto.UpdateTokenRequestDto
import com.devkot.teammates.data.remote.dto.UpdateTokenResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface TeammatesAuthApiService {

        @POST("login")
        suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

        @POST("update_tokens")
        suspend fun updateTokens(@Body request: UpdateTokenRequestDto): UpdateTokenResponseDto

}