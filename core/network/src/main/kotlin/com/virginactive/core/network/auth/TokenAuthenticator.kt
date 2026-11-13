package com.virginactive.core.network.auth

import okhttp3.Authenticator
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

internal class TokenAuthenticator(
    private val base: HttpUrl,
    private val tokens: TokenStore,
    private val refresher: TokenRefresher,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val request = response.request
        if (!request.needsAuthentication(base)) return null
        return tokens.locked {
            val current = tokens.current ?: return@locked null
            val rejected = request.header("Authorization")
            if (response.priorResponse != null) {
                if (rejected == current.bearer) tokens.clear()
                return@locked null
            }
            val fresh =
                if (rejected != current.bearer) current
                else refresher.refresh(current) ?: return@locked null
            request.newBuilder().header("Authorization", fresh.bearer).build()
        }
    }
}
