package com.pezont.teammates.domain.usecase

import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoadUserUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(): User {
        return userDataRepository.user.first()
    }
}