package com.pezont.teammates.network

import com.pezont.teammates.models.LoginAuthRequest
import com.pezont.teammates.models.LoginAuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TeammatesAuthApiService {


        @POST("login")
        suspend fun login(@Body request: LoginAuthRequest): LoginAuthResponse



//    @POST("register")
//    suspend fun register(@Body request: RegisterRequest): RegisterResponse


}