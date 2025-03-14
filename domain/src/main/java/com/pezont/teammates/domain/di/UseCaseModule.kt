package com.pezont.teammates.domain.di

import com.pezont.teammates.domain.repository.AuthRepository
import com.pezont.teammates.domain.repository.QuestionnairesRepository
import com.pezont.teammates.domain.repository.UserDataRepository
import com.pezont.teammates.domain.usecase.CheckAuthenticationUseCase
import com.pezont.teammates.domain.usecase.LoadQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoginUseCase
import com.pezont.teammates.domain.usecase.LogoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideLoginUseCase(
        authRepository: AuthRepository,
        userDataRepository: UserDataRepository
    ): LoginUseCase {
        return LoginUseCase(authRepository, userDataRepository)
    }

    @Provides
    fun provideLogoutUseCase(
        userDataRepository: UserDataRepository
    ): LogoutUseCase {
        return LogoutUseCase(userDataRepository)
    }

    @Provides
    fun provideCheckAuthenticationUseCase(
        userDataRepository: UserDataRepository
    ): CheckAuthenticationUseCase {
        return CheckAuthenticationUseCase(userDataRepository)
    }

    @Provides
    fun provideGetQuestionnairesUseCase(
        questionnairesRepository: QuestionnairesRepository,
        userDataRepository: UserDataRepository
    ): LoadQuestionnairesUseCase {
        return LoadQuestionnairesUseCase(questionnairesRepository, userDataRepository)
    }
//
//    @Provides
//    fun provideGetUserQuestionnairesUseCase(
//        questionnairesRepository: QuestionnairesRepository,
//        userDataRepository: UserDataRepository
//    ): GetUserQuestionnairesUseCase {
//        return GetUserQuestionnairesUseCase(questionnairesRepository, userDataRepository)
//    }
//
//    @Provides
//    fun provideGetLikedQuestionnairesUseCase(
//        questionnairesRepository: QuestionnairesRepository,
//        userDataRepository: UserDataRepository
//    ): GetLikedQuestionnairesUseCase {
//        return GetLikedQuestionnairesUseCase(questionnairesRepository, userDataRepository)
//    }
//
//    @Provides
//    fun provideCreateQuestionnaireUseCase(
//        questionnairesRepository: QuestionnairesRepository,
//        userDataRepository: UserDataRepository
//    ): CreateQuestionnaireUseCase {
//        return CreateQuestionnaireUseCase(questionnairesRepository, userDataRepository)
//    }
}
