package io.pleo.antaeus.core.schedulers

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.quartz.Scheduler
import org.quartz.Trigger
import java.util.function.Supplier

class RecurringTaskSchedulerTest {
    private val taskSlot = CapturingSlot<Trigger>()
    //Test config
    private val scheduler = mockk<Scheduler> {
        every { scheduleJob(any(), capture(taskSlot)) } answers {
            println(taskSlot.captured)
            null
        }
        every { start() } answers {

        }
    }
    private val function = Supplier { }

    //Inject Mocks
    private val recurringTaskScheduler = RecurringTaskScheduler(scheduler = scheduler)

    @Test
    fun `will schedule the passed function`() {

        recurringTaskScheduler.scheduleMonthly(function)

        val captured: Trigger = taskSlot.captured
        assertTrue {
            captured.jobDataMap.containsKey("function")
        }
        verify(exactly = 1) { scheduler.scheduleJob(any(), captured) }
    }
}