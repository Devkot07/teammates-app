package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.repository.UserDataRepository
import java.time.Instant
import javax.inject.Inject

class CheckAuthenticationUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(): Boolean {
        val nowTimeInMillis = Instant.now().toEpochMilli()
        return nowTimeInMillis < userDataRepository.refreshTokenExpirationTime()
    }


}
