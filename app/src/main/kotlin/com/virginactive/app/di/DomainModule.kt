package com.virginactive.app.di

import com.virginactive.core.domain.BookClassUseCase
import com.virginactive.core.domain.GetClassUseCase
import com.virginactive.core.domain.repositories.FitnessRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    fun bookClassUseCase(repository: FitnessRepository, clock: Clock): BookClassUseCase =
        BookClassUseCase(repository, clock)

    @Provides
    fun getClassUseCase(repository: FitnessRepository): GetClassUseCase =
        GetClassUseCase(repository)
}
