package me.paulohcardoson.appointments.app.services;

import me.paulohcardoson.appointments.app.dto.responses.StatsResponse;
import me.paulohcardoson.appointments.app.repositories.AppointmentRepository;
import me.paulohcardoson.appointments.app.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;

@Service
public class StatsService {

	private final PatientRepository patientRepository;
	private final AppointmentRepository appointmentRepository;

	public StatsService(PatientRepository patientRepository, AppointmentRepository appointmentRepository) {
		this.patientRepository = patientRepository;
		this.appointmentRepository = appointmentRepository;
	}

	public StatsResponse getStats() {
		long totalPatients = patientRepository.count();

		Instant startOfDay = Instant.now().atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
		Instant endOfDay = startOfDay.atZone(ZoneOffset.UTC).plusDays(1).toInstant();

		long todayAppointments = appointmentRepository.countByDay(startOfDay, endOfDay);

		return new StatsResponse(totalPatients, todayAppointments);
	}
}
