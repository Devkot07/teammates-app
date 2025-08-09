package com.devkot.teammates.data.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

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
        gameName: String?,
        authorId: String?,
        questionnaireId: String?
    ): List<QuestionnaireEntity>



    @Upsert(QuestionnaireEntity::class)
    suspend fun insertQuestionnaires(questionnaire: QuestionnaireEntity)




}