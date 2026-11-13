package com.virginactive.app.di

import com.virginactive.core.data.AuthRepositoryImpl
import com.virginactive.core.data.FitnessRepositoryImpl
import com.virginactive.core.domain.repositories.AuthRepository
import com.virginactive.core.domain.repositories.FitnessRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun auth(implementation: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton abstract fun fitness(implementation: FitnessRepositoryImpl): FitnessRepository
}
