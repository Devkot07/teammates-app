package com.devkot.teammates.data.remote.interceptor

import android.util.Log
import com.devkot.teammates.domain.repository.UserDataRepository
import com.devkot.teammates.domain.usecase.UpdateTokensUseCase
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
            response.close()

            val refreshed = runBlocking {
                mutex.withLock {
                    try {
                        val currentToken = userDataRepository.accessToken()
                        val stillUnauthorized =
                            currentToken == originalRequest.header("Authorization")
                                ?.removePrefix("Bearer ")

                        if (stillUnauthorized) {
                            Log.w(TAG, "Refreshing tokens...")
                            updateTokensUseCase.get().invoke().isSuccess
                        } else {
                            Log.d(
                                TAG,
                                "Token already refreshed by another request, skipping refresh."
                            )
                            true
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Token refresh failed", e)
                        false
                    }
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
                return response
            }
        }
        return response
    }


    companion object {
        private const val TAG = "TokenRefreshInterceptor"
        private val mutex = Mutex()
    }
}