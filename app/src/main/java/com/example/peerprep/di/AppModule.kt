package com.example.peerprep.di

import com.example.peerprep.data.repository.FirebaseUserRepository
import com.example.peerprep.data.repository.LessonRepositoryImpl
import com.example.peerprep.domain.repository.LessonRepository
import com.example.peerprep.domain.usecase.GetLessonsUseCase
import com.example.peerprep.domain.usecase.SignUpUseCase
import com.example.peerprep.presentation.navigation.NavigationManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseUserRepository(firestore: FirebaseFirestore): FirebaseUserRepository =
        FirebaseUserRepository(firestore)

    @Provides
    fun provideSignUpUseCase(repository: FirebaseUserRepository): SignUpUseCase =
        SignUpUseCase(repository)

    @Provides
    @Singleton
    fun provideNavigationManager(): NavigationManager = NavigationManager()

    @Provides
    @Singleton
    fun provideLessonRepository(): LessonRepository {
        return LessonRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideGetLessonsUseCase(lessonRepository: LessonRepository): GetLessonsUseCase {
        return GetLessonsUseCase(lessonRepository)
    }
}

