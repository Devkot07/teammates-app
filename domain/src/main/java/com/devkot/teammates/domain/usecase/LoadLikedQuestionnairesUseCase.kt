package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.repository.UserDataRepository
import com.devkot.teammates.domain.repository.UsersRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoadLikedQuestionnairesUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(): Result<List<Questionnaire>> {
        return runCatching {

            val user = userDataRepository.user.first()

            usersRepository.loadLikedQuestionnaires(
                token = userDataRepository.accessToken.first(),
                userId = user.publicId,
            ).getOrThrow()
        }
    }
}
