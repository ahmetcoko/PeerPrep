package com.example.peerprep.di

import com.example.peerprep.data.repository.FirebaseUserRepository
import com.example.peerprep.domain.usecase.SignUpUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object AppModule {


    @Provides
    fun provideFirebaseUserRepository(): FirebaseUserRepository = FirebaseUserRepository()


    @Provides
    fun provideSignUpUseCase(repository: FirebaseUserRepository): SignUpUseCase = SignUpUseCase(repository)
}
