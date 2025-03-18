package com.pezont.teammates.domain.usecase

import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.repository.UserDataRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return runCatching {
            userDataRepository.saveAccessToken("")
            userDataRepository.saveRefreshToken("")
            userDataRepository.saveUser(User())
            Result.success(Unit)
        }
    }
}