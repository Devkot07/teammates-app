package com.pezont.teammates.domain.di

import com.pezont.teammates.domain.repository.AuthRepository
import com.pezont.teammates.domain.repository.QuestionnairesRepository
import com.pezont.teammates.domain.repository.UserDataRepository
import com.pezont.teammates.domain.repository.UsersRepository
import com.pezont.teammates.domain.usecase.CheckAuthenticationUseCase
import com.pezont.teammates.domain.usecase.CreateQuestionnaireUseCase
import com.pezont.teammates.domain.usecase.LoadLikedQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadUserUseCase
import com.pezont.teammates.domain.usecase.LoadUserQuestionnairesUseCase
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
    fun provideLoadUserUseCase(
        userDataRepository: UserDataRepository
    ): LoadUserUseCase {
        return LoadUserUseCase(userDataRepository)
    }

    @Provides
    fun provideLoadQuestionnairesUseCase(
        questionnairesRepository: QuestionnairesRepository,
        userDataRepository: UserDataRepository
    ): LoadQuestionnairesUseCase {
        return LoadQuestionnairesUseCase(questionnairesRepository, userDataRepository)
    }

    @Provides
    fun provideLoadUserQuestionnairesUseCase(
        questionnairesRepository: QuestionnairesRepository,
        userDataRepository: UserDataRepository
    ): LoadUserQuestionnairesUseCase {
        return LoadUserQuestionnairesUseCase(questionnairesRepository, userDataRepository)
    }

    @Provides
    fun provideLoadLikedQuestionnairesUseCase(
        usersRepository: UsersRepository,
        userDataRepository: UserDataRepository
    ): LoadLikedQuestionnairesUseCase {
        return LoadLikedQuestionnairesUseCase(usersRepository, userDataRepository)
    }

    @Provides
    fun provideCreateQuestionnaireUseCase(
        questionnairesRepository: QuestionnairesRepository,
        userDataRepository: UserDataRepository
    ): CreateQuestionnaireUseCase {
        return CreateQuestionnaireUseCase(questionnairesRepository, userDataRepository)
    }
}
