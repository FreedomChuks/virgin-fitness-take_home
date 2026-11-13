package com.virginactive.core.network

import java.time.Clock
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient

fun createHttpClient(
    baseUrl: String,
    tokens: TokenStore,
    json: Json,
    clock: Clock = Clock.systemUTC(),
): OkHttpClient {
    val base =
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .followRedirects(false)
            .followSslRedirects(false)
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder().header("Accept", "application/json").build())
            }
            .build()
    val url = baseUrl.toHttpUrl()
    val refresher = TokenRefresher(url, tokens, base, json, clock)
    return base
        .newBuilder()
        .addInterceptor(AuthInterceptor(url, tokens, refresher))
        .authenticator(TokenAuthenticator(url, tokens, refresher))
        .build()
}
