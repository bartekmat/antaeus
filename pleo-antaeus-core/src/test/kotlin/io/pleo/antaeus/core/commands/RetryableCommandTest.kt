package io.pleo.antaeus.core.commands

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.exceptions.MultipleNetworkException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.function.Supplier

class RetryableCommandTest {
    private val expectedException = NetworkException()
    private val maxTries = 3

    private val command = RetryableCommand<Boolean>(maxTries)

    @Test
    fun `will run once if no exception thrown`() {
        val function = mockk<Supplier<Boolean>>() {
            every { get() } returns true
        }
        command.run(function)
        verify(exactly = 1) { function.get() }
    }

    @Test
    fun `will run twice if first throws exception`() {
        var counter = 1
        val function = mockk<Supplier<Boolean>>() {
            every { get() } answers {
                if (counter == 1) {
                    counter++
                    throw expectedException
                }
                true
            }
        }
        command.run(function)
        verify(exactly = 2) { function.get() }
    }

    @Test
    fun `will run three times if first two throw exception`() {
        var counter = 1
        val function = mockk<Supplier<Boolean>>() {
            every { get() } answers {
                if (counter <= 2) {
                    counter++
                    throw expectedException
                }
                true
            }
        }
        command.run(function)
        verify(exactly = 3) { function.get() }
    }

    @Test
    fun `will throw if exceedes number of tries`() {
        val function = mockk<Supplier<Boolean>>() {
            every { get() } throws expectedException
        }
        assertThrows<MultipleNetworkException> {
            command.run(function)
            verify(exactly = 4) { function.get() }
        }
    }

    @Test
    fun `will throw if currency mismatch is thrown`() {
        val function = mockk<Supplier<Boolean>>() {
            every { get() } throws CurrencyMismatchException(1, 1)
        }
        assertThrows<CurrencyMismatchException> {
            command.run(function)
        }
    }
}