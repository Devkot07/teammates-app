package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.repository.UserDataRepository
import javax.inject.Inject

class LoadUserUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(): User {
        return userDataRepository.user()
    }
}