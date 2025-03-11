package com.pezont.teammates.data

import com.pezont.teammates.domain.model.LoginRequest
import com.pezont.teammates.domain.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TeammatesAuthApiService {


        @POST("login")
        suspend fun login(@Body request: LoginRequest): LoginResponse



//    @POST("register")
//    suspend fun register(@Body request: RegisterRequest): RegisterResponse


}