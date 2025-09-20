package com.devkot.teammates.data.remote.interceptor

import com.devkot.teammates.domain.repository.UserDataRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthHeaderInterceptor @Inject constructor(
    private val userDataRepository: UserDataRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val token = runBlocking { userDataRepository.accessToken() }

        val newRequest = original.newBuilder()
            .header("accept", "application/json")
            .apply {
                header("Authorization", "Bearer $token")
            }
            .build()

        return chain.proceed(newRequest)
    }
}
