package com.pezont.teammates.domain.usecase

import com.pezont.teammates.domain.model.Games
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.repository.QuestionnairesRepository
import com.pezont.teammates.domain.repository.UserDataRepository
import com.pezont.teammates.domain.repository.UsersRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoadLikedQuestionnairesUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(): Result<List<Questionnaire>> {
        return runCatching {

            val user = userDataRepository.user.first()
            if (user.publicId == null) throw Exception("User not authenticated")

            usersRepository.loadLikedQuestionnaires(
                token = userDataRepository.accessToken.first(),
                userId = user.publicId,
            ).getOrThrow()
        }
    }
}
