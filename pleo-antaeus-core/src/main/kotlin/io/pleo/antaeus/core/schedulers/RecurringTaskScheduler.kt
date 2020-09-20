package io.pleo.antaeus.core.schedulers

import io.pleo.antaeus.core.tasks.RecurringTask
import org.quartz.*
import org.quartz.JobBuilder.newJob
import org.quartz.impl.StdSchedulerFactory
import java.util.function.Supplier

class RecurringTaskScheduler (
        private val scheduler: Scheduler = StdSchedulerFactory.getDefaultScheduler()
) {
    fun scheduleMonthly(function: Supplier<Unit>) {
        scheduler.start()

        val job: JobDetail = newJob(RecurringTask::class.java).withIdentity("monthlyTask", "group1").build()

        val jobDataMap = JobDataMap()
        jobDataMap["function"] = function

        val trigger: Trigger = TriggerBuilder.newTrigger()
                .withIdentity("monthlyTrigger")
                //.withSchedule(CronScheduleBuilder.monthlyOnDayAndHourAndMinute(1,0,0))
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *"))
                .usingJobData(jobDataMap)
                .forJob(job)
                .build()

        scheduler.scheduleJob(job, trigger)
    }
}