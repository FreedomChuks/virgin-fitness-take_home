package com.virginactive.core.data

import com.virginactive.core.domain.AppFailure
import com.virginactive.core.domain.Result
import com.virginactive.core.network.ApiException
import com.virginactive.core.network.ServerResponseException
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class RepositoryResultTest {
    @Test
    fun `successful mapping returns data`() = runTest {
        assertEquals(Result.Success(42), mapped { 21 * 2 })
    }

    @Test
    fun `API failure preserves typed reason status and request id`() = runTest {
        val result = mapped<Unit> { throw ApiException(401, "Sign in again", "req-1") }
        val failure = (result as Result.Failure).error
        assertEquals(AppFailure.Reason.UNAUTHORIZED, failure.reason)
        assertEquals(401, failure.code)
        assertEquals("req-1", failure.requestId)
        assertEquals("Sign in again", failure.message)
    }

    @Test
    fun `transport and mapping errors produce distinct failures`() = runTest {
        val transport = mapped<Unit> { throw IOException("Timeout") } as Result.Failure
        val mapping =
            mapped<Unit> { throw IllegalArgumentException("Malformed DTO") } as Result.Failure
        assertEquals(AppFailure.Reason.NETWORK, transport.error.reason)
        assertEquals(AppFailure.Reason.UNKNOWN, mapping.error.reason)
    }

    @Test
    fun `cancellation propagates without becoming failure`() = runTest {
        val cancellation = CancellationException("Cancelled request")
        try {
            mapped<Unit> { throw cancellation }
            fail("Expected cancellation")
        } catch (caught: CancellationException) {
            assertEquals(cancellation.message, caught.message)
        }
    }

    @Test
    fun `fatal errors are not converted into ordinary failures`() = runTest {
        val fatal = AssertionError("Programming invariant")
        try {
            mapped<Unit> { throw fatal }
            fail("Expected original error")
        } catch (caught: AssertionError) {
            assertEquals(fatal.message, caught.message)
        }
    }

    @Test
    fun `reads retry transient failures up to three attempts`() = runTest {
        var attempts = 0
        val result =
            mapped(retry = true) {
                if (++attempts < 3) throw ApiException(500, "Busy", null)
                "ok"
            }
        assertEquals(Result.Success("ok"), result)
        assertEquals(3, attempts)
    }

    @Test
    fun `read retries are bounded`() = runTest {
        var attempts = 0
        val result =
            mapped<Unit>(retry = true) {
                attempts++
                throw IOException("Timeout")
            }
        assertEquals(AppFailure.Reason.NETWORK, (result as Result.Failure).error.reason)
        assertEquals(3, attempts)
    }

    @Test
    fun `programming errors are not hidden as unreadable data`() = runTest {
        try {
            mapped<Unit> { error("bug") }
            fail("Expected original error")
        } catch (caught: IllegalStateException) {
            assertEquals("bug", caught.message)
        }
    }

    @Test
    fun `server response problems keep their message`() = runTest {
        val failure =
            (mapped<Unit> { throw ServerResponseException("Couldn't refresh the session (500).") }
                    as Result.Failure)
                .error
        assertEquals(AppFailure.Reason.SERVER, failure.reason)
        assertEquals("Couldn't refresh the session (500).", failure.message)
    }

    @Test
    fun `retries back off before each attempt`() = runTest {
        var attempts = 0
        mapped(retry = true) {
            if (++attempts < 3) throw ApiException(429, "Slow down", null)
            "ok"
        }
        assertEquals(1_500L, currentTime)
    }

    @Test
    fun `writes and client errors are never retried`() = runTest {
        var writes = 0
        mapped<Unit> {
            writes++
            throw IOException("Timeout")
        }
        var invalid = 0
        mapped<Unit>(retry = true) {
            invalid++
            throw ApiException(404, "Missing", null)
        }
        assertEquals(1, writes)
        assertEquals(1, invalid)
    }
}
