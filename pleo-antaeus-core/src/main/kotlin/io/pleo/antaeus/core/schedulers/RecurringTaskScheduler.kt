package io.pleo.antaeus.core.schedulers

import io.pleo.antaeus.core.tasks.RecurringTask
import kotlinx.coroutines.Job
import org.quartz.*
import org.quartz.JobBuilder.newJob
import org.quartz.impl.StdSchedulerFactory
import java.util.function.Supplier

class RecurringTaskScheduler (
        private val scheduler: Scheduler = StdSchedulerFactory.getDefaultScheduler(),
        private val cron: CronScheduleBuilder? = CronScheduleBuilder.monthlyOnDayAndHourAndMinute(1,0,0),
        private val job: JobDetail = newJob(RecurringTask::class.java).withIdentity("monthlyTask", "group1").build()
) {
    fun scheduleMonthly(function: Supplier<Job>) {
        scheduler.start()

        val jobDataMap = JobDataMap()
        jobDataMap["function"] = function

        val trigger: Trigger = TriggerBuilder.newTrigger()
                .withIdentity("monthlyTrigger")
                //.withSchedule(cron)
                //for manual testing - every minute
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *"))
                .usingJobData(jobDataMap)
                .forJob(job)
                .build()

        scheduler.scheduleJob(job, trigger)
    }
}