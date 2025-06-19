package com.pezont.teammates.domain.repository

import com.pezont.teammates.domain.model.LoadAuthorRequest
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.UpdateUserProfileRequest
import com.pezont.teammates.domain.model.User

interface UsersRepository {

    suspend fun loadLikedQuestionnaires(
        token: String,
        userId: String,
    ): Result<List<Questionnaire>>

    suspend fun loadAuthorProfile(
        token: String,
        userId: String,
        request: LoadAuthorRequest,
    ): Result<User>

    suspend fun updateUserProfile(
        token: String,
        userId: String,
        request: UpdateUserProfileRequest,
    ): Result<User>

}
