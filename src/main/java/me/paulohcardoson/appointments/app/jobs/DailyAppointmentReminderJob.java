package me.paulohcardoson.appointments.app.jobs;

import me.paulohcardoson.appointments.app.models.Appointment;
import me.paulohcardoson.appointments.app.repositories.AppointmentRepository;
import me.paulohcardoson.appointments.app.services.WhatsAppService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
public class DailyAppointmentReminderJob implements Job {

	private static final ZoneId MANAUS_ZONE = ZoneId.of("America/Manaus");

	private final AppointmentRepository appointmentRepository;
	private final WhatsAppService whatsAppService;

	public DailyAppointmentReminderJob(AppointmentRepository appointmentRepository, WhatsAppService whatsAppService) {
		this.appointmentRepository = appointmentRepository;
		this.whatsAppService = whatsAppService;
	}

	@Override
	public void execute(JobExecutionContext context) {
		LocalDate tomorrow = LocalDate.now(MANAUS_ZONE).plusDays(1);
		Instant startOfDay = tomorrow.atStartOfDay(MANAUS_ZONE).toInstant();
		Instant endOfDay = tomorrow.plusDays(1).atStartOfDay(MANAUS_ZONE).toInstant();

		List<Appointment> appointments = appointmentRepository.findNotConfirmedByDay(startOfDay, endOfDay);
		for (Appointment appointment : appointments) {
			whatsAppService.sendAppointmentReminder(appointment);
		}
	}
}
