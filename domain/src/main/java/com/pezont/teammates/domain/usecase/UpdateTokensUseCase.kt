package com.pezont.teammates.domain.usecase

import android.util.Log
import com.pezont.teammates.domain.model.UpdateTokenRequest
import com.pezont.teammates.domain.repository.AuthRepository
import com.pezont.teammates.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import javax.inject.Inject

class UpdateTokensUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        val refreshToken = userDataRepository.refreshToken.first()
        val publicId = userDataRepository.user.first().publicId
        Log.d("UpdateTokensUseCase", "Tokens updated successfully. Saving new tokens")

        val response =
            authRepository.updateTokens(UpdateTokenRequest(publicId, refreshToken)).getOrThrow()
        val nowTimeInMillis = Instant.now().toEpochMilli()
        val refreshTokenExpirationTime = nowTimeInMillis + 1000L*60L*60L*24L*100L

        userDataRepository.saveAccessToken(response.accessToken)
        userDataRepository.saveRefreshToken(response.refreshToken)
        userDataRepository.saveRefreshTokenExpirationTime(refreshTokenExpirationTime)
    }
}