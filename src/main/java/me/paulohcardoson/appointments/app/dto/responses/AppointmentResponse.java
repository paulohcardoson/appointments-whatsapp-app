package me.paulohcardoson.appointments.app.dto.responses;

import lombok.Builder;
import me.paulohcardoson.appointments.app.models.Appointment;

import java.time.Instant;

@Builder
public class AppointmentResponse {
	public Long id;
	public Instant startTime;
	public Instant endTime;
	public Patient patient;

	public static AppointmentResponse of(Appointment appointment) {
		var patient = appointment.patient;

		return AppointmentResponse.builder()
			.id(appointment.id)
			.startTime(appointment.startTime)
			.endTime(appointment.endTime)
			.patient(Patient.builder()
				.id(patient.id)
				.fullName(patient.fullName)
				.phoneNumber(patient.phoneNumber)
				.cpf(patient.cpf)
				.build()
			)
			.build();
	}

	@Builder
	public static class Patient {
		public Long id;
		public String fullName;
		public String phoneNumber;
		public String cpf;
	}
}
