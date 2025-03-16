package com.pezont.teammates.domain.repository

import com.pezont.teammates.domain.model.Questionnaire

interface UsersRepository {

    suspend fun loadLikedQuestionnaires(
        token: String,
        userId: String,
    ): Result<List<Questionnaire>>

}
