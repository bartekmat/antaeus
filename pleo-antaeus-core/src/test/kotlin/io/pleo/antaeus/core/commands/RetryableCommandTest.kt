package io.pleo.antaeus.core.commands

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pleo.antaeus.core.exceptions.MultipleTryFailedException
import io.pleo.antaeus.core.exceptions.NetworkException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.function.Supplier

class RetryableCommandTest {

    val command = RetryableCommand<Boolean>(3)

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
                    throw NetworkException()
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
                    throw NetworkException()
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
            every { get() } throws NetworkException()
        }
        assertThrows<MultipleTryFailedException> {
            command.run(function)
        }
    }
}