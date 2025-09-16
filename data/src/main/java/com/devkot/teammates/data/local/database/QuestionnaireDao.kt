package com.devkot.teammates.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.devkot.teammates.domain.model.Questionnaire

@Dao
interface QuestionnaireDao {

    @Query("SELECT * FROM questionnaire")
    suspend fun getAllQuestionnaires(): List<QuestionnaireEntity>

    @Query("""
    SELECT * FROM questionnaire
      WHERE (:gameName IS NULL OR game = :gameName)
      AND (:authorId IS NULL OR author_id = :authorId)
      AND (:questionnaireId IS NULL OR questionnaire_id = :questionnaireId)
    LIMIT :limit OFFSET (:page - 1) * :limit
""")
    suspend fun getFilteredQuestionnaires(
        page: Int = 1,
        limit: Int = 10,
        gameName: String? = null,
        authorId: String? = null,
        questionnaireId: String? = null
    ): List<QuestionnaireEntity>


    @Query("""
    SELECT * FROM like_questionnaire""")
    suspend fun getLikedQuestionnaires(): List<QuestionnaireEntity>


    @Upsert
    suspend fun insertQuestionnaire(questionnaire: QuestionnaireEntity)

    @Upsert
    suspend fun insertLikeQuestionnaire(likeQuestionnaire: LikeQuestionnaireEntity)

    @Upsert
    suspend fun insertQuestionnaires(questionnaires: List<QuestionnaireEntity>)

    @Upsert
    suspend fun insertLikeQuestionnaires(likeQuestionnaires: List<LikeQuestionnaireEntity>)


    @Delete
    suspend fun deleteQuestionnaire(questionnaire: QuestionnaireEntity)

    @Delete
    suspend fun deleteLikeQuestionnaire(questionnaire: LikeQuestionnaireEntity)

    @Transaction
    suspend fun deleteQuestionnaireCompletely(questionnaire: Questionnaire) {
        deleteQuestionnaire(questionnaire.toDefaultEntity())
        deleteLikeQuestionnaire(questionnaire.toLikeEntity())
    }

}