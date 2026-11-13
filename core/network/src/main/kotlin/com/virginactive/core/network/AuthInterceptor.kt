package com.virginactive.core.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

internal class AuthInterceptor(
    private val base: HttpUrl,
    private val tokens: TokenStore,
    private val refresher: TokenRefresher,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!request.needsAuthentication(base)) {
            return chain.proceed(request.newBuilder().removeHeader("Authorization").build())
        }
        val current = tokens.current ?: return signedOut(request)
        val usable = if (refresher.isExpiring(current)) refresher.refreshIfExpiring() else current
        if (usable == null) return signedOut(request)
        return chain.proceed(request.newBuilder().header("Authorization", usable.bearer).build())
    }

    private fun signedOut(request: Request): Response =
        Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body(
                """{"message":"Your session has ended. Please sign in again."}"""
                    .toResponseBody("application/json".toMediaType()))
            .build()
}

internal fun Request.needsAuthentication(base: HttpUrl): Boolean {
    if (url.scheme != base.scheme || url.host != base.host || url.port != base.port) return false
    if (!url.encodedPath.startsWith(base.encodedPath)) return false
    val relativePath = url.encodedPath.removePrefix(base.encodedPath)
    return relativePath != "auth/login" && relativePath != "auth/refresh"
}
