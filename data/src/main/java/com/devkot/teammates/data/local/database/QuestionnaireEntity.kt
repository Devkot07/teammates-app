package com.devkot.teammates.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devkot.teammates.domain.model.Questionnaire

data class QuestionnaireFields(
    @ColumnInfo(name = "header") val header: String,
    @ColumnInfo(name = "game") val game: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "author_id") val authorId: String,
    @ColumnInfo(name = "image_path") val imagePath: String
)

@Entity(tableName = "questionnaire")
data class QuestionnaireEntity(
    @PrimaryKey
    @ColumnInfo(name = "questionnaire_id")
    val questionnaireId: String,
    @Embedded val fields: QuestionnaireFields
)

@Entity(tableName = "like_questionnaire")
data class LikeQuestionnaireEntity(
    @PrimaryKey
    @ColumnInfo(name = "questionnaire_id")
    val questionnaireId: String,
    @Embedded val fields: QuestionnaireFields
)


fun QuestionnaireFields.toDomain(id: String) = Questionnaire(
    header = header,
    game = game,
    description = description,
    authorId = authorId,
    questionnaireId = id,
    imagePath = imagePath
)

fun Questionnaire.toFields() = QuestionnaireFields(
    header = header,
    game = game,
    description = description,
    authorId = authorId,
    imagePath = imagePath
)

fun QuestionnaireEntity.toDomain() = fields.toDomain(questionnaireId)
fun LikeQuestionnaireEntity.toDomain() = fields.toDomain(questionnaireId)

fun Questionnaire.toDefaultEntity() = QuestionnaireEntity(questionnaireId, toFields())
fun Questionnaire.toLikeEntity() = LikeQuestionnaireEntity(questionnaireId, toFields())

