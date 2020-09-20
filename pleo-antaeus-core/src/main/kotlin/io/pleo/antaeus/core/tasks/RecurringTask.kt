package io.pleo.antaeus.core.tasks

import org.quartz.Job
import org.quartz.JobExecutionContext
import java.util.function.Supplier

class RecurringTask : Job {

    override fun execute(context: JobExecutionContext?) {
        val task: Supplier<Unit> = context?.mergedJobDataMap?.get("function") as Supplier<Unit>
        task.get()
    }

}
