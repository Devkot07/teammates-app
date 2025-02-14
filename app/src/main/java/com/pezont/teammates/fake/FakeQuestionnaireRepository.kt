package com.pezont.teammates.fake

import com.pezont.teammates.models.Questionnaire

class FakeQuestionnaireRepository {
    fun getRecommendedQuestionnaires(): List<Questionnaire> {
        return FakeDataStore(0).questionnaireList
    }

    fun getNextRecommendedQuestionnaires(i: Int): List<Questionnaire> {
        return FakeDataStore(i).questionnaireList2
    }


}