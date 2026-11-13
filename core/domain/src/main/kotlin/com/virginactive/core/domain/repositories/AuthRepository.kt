package com.virginactive.core.domain.repositories

import com.virginactive.core.domain.Result
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val signedIn: StateFlow<Boolean>

    suspend fun login(username: String, password: String): Result<Unit>

    suspend fun logout(): Result<Unit>
}
