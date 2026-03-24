package me.paulohcardoson.appointments.app.config;

import me.paulohcardoson.appointments.app.jobs.DailyAppointmentReminderJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class QuartzConfig {

	@Bean
	public JobDetail dailyAppointmentReminderJobDetail() {
		return JobBuilder.newJob(DailyAppointmentReminderJob.class)
			.withIdentity("dailyAppointmentReminderJob")
			.storeDurably()
			.build();
	}

	@Bean
	public Trigger dailyAppointmentReminderTrigger(JobDetail dailyAppointmentReminderJobDetail) {
		CronScheduleBuilder schedule = CronScheduleBuilder
			//.cronSchedule("0 0 5 * * ?")
			.cronSchedule("0 * * * * ?")
			.inTimeZone(TimeZone.getTimeZone("America/Manaus"));

		return TriggerBuilder.newTrigger()
			.forJob(dailyAppointmentReminderJobDetail)
			.withIdentity("dailyAppointmentReminderTrigger")
			.withSchedule(schedule)
			.build();
	}
}
