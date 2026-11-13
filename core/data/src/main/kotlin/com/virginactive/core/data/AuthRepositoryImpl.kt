package com.virginactive.core.data

import com.virginactive.core.domain.repositories.AuthRepository
import com.virginactive.core.network.NetworkDataSource
import com.virginactive.core.network.auth.TokenStore
import javax.inject.Inject
import javax.inject.Qualifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class IoDispatcher

class AuthRepositoryImpl
@Inject
constructor(
    private val network: NetworkDataSource,
    private val tokens: TokenStore,
    private val members: MemberCache,
    @param:IoDispatcher private val io: CoroutineDispatcher,
) : AuthRepository {
    override val signedIn = tokens.signedIn

    override suspend fun login(username: String, password: String) =
        withContext(io) {
            mapped {
                val login = network.login(username.trim(), password)
                tokens.locked {
                    tokens.save(login.tokens)
                    members.member = login.member?.toMember()
                }
            }
        }

    override suspend fun logout() =
        withContext(io) {
            mapped {
                network.cancelInFlight()
                tokens.locked {
                    tokens.clear()
                    members.member = null
                    network.cancelInFlight()
                }
            }
        }
}
