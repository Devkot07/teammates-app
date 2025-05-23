package com.pezont.teammates.domain.usecase

import com.pezont.teammates.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CheckAuthenticationUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(): Flow<Boolean> =
        userDataRepository.user.map { it.publicId != "" }
}
