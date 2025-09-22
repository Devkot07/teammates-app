package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.repository.UserDataRepository
import com.devkot.teammates.domain.repository.UsersRepository
import javax.inject.Inject

class LoadLikedQuestionnairesUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(): Result<Pair<List<Questionnaire>, Throwable?>> {
        return runCatching {

            val user = userDataRepository.user()

            usersRepository.loadLikedQuestionnaires(
                userId = user.publicId,
            ).getOrThrow()
        }
    }
}
