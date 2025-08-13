package com.devkot.teammates.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [QuestionnaireEntity::class, LikeQuestionnaireEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TeammatesDatabase : RoomDatabase() {
    abstract fun questionnaireDao(): QuestionnaireDao
}
