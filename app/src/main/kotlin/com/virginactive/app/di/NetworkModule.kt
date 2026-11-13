package com.virginactive.app.di

import com.virginactive.app.BuildConfig
import com.virginactive.core.data.IoDispatcher
import com.virginactive.core.network.NetworkDataSource
import com.virginactive.core.network.auth.TokenStore
import com.virginactive.core.network.createHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton fun json(): Json = Json { ignoreUnknownKeys = true }

    @Provides @Singleton fun clock(): Clock = Clock.systemUTC()

    @Provides @IoDispatcher fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides @Singleton fun tokenStore(): TokenStore = TokenStore()

    @Provides
    @Singleton
    fun httpClient(tokens: TokenStore, json: Json, clock: Clock): OkHttpClient =
        createHttpClient(BuildConfig.API_BASE_URL, tokens, json, clock)

    @Provides
    @Singleton
    fun networkDataSource(httpClient: OkHttpClient, json: Json, clock: Clock): NetworkDataSource =
        NetworkDataSource(BuildConfig.API_BASE_URL, httpClient, json, clock)
}
