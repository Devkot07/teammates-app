package com.devkot.teammates.data.remote.interceptor

import android.util.Log
import com.devkot.teammates.domain.repository.UserDataRepository
import com.devkot.teammates.domain.usecase.UpdateTokensUseCase
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefreshInterceptor @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val updateTokensUseCase: Lazy<UpdateTokensUseCase>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
        val hasAuthHeader = originalRequest.header("Authorization") != null
        val response = chain.proceed(originalRequest)

        if (response.code == 401 && hasAuthHeader) {

            Log.w(TAG, "Received 401 Unauthorized. Attempting token refresh.")
            response.close()

            val refreshed = runBlocking {
                try {
                    val result = updateTokensUseCase.get().invoke()
                    Log.d(TAG, "Token refresh result: $result")
                    result.isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Token refresh failed with exception: ${e.message}", e)
                    false
                }
            }

            if (refreshed) {
                val newToken = runBlocking { userDataRepository.accessToken() }
                Log.d(TAG, "New token retrieved. Retrying request with refreshed token.")

                val newRequest = originalRequest.newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer $newToken")
                    .build()

                return chain.proceed(newRequest)
            } else {
                Log.e(TAG, "Token refresh failed (possibly expired refresh token)")
                return chain.proceed(originalRequest)
            }
        }
        return response
    }


    companion object {
        private const val TAG = "TokenRefreshInterceptor"
    }
}