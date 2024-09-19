package com.example.peerprep.di

import android.content.Context
import com.example.peerprep.data.repository.FirebasePostRepository
import com.example.peerprep.data.repository.FirebaseUserRepository
import com.example.peerprep.data.repository.LessonRepositoryImpl
import com.example.peerprep.data.repository.UniversityRepositoryImpl
import com.example.peerprep.domain.repository.LessonRepository
import com.example.peerprep.domain.repository.UniversityRepository
import com.example.peerprep.domain.usecase.GetLessonsUseCase
import com.example.peerprep.domain.usecase.SignUpUseCase
import com.example.peerprep.presentation.navigation.NavigationManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseUserRepository(firestore: FirebaseFirestore): FirebaseUserRepository =
        FirebaseUserRepository(firestore)

    @Provides
    @Singleton
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

    @Provides
    @Singleton
    fun provideFirebasePostRepository(firestore: FirebaseFirestore): FirebasePostRepository {
        return FirebasePostRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideUniversityRepository(
        @ApplicationContext context: Context
    ): UniversityRepository {
        return UniversityRepositoryImpl(context)
    }
}

