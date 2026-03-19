package me.paulohcardoson.appointments.app.dto.responses;

import lombok.Builder;
import me.paulohcardoson.appointments.app.enums.AppointmentStatus;
import me.paulohcardoson.appointments.app.models.Appointment;

import java.time.Instant;

@Builder
public class AppointmentResponse {
	public Long id;
	public Instant startTime;
	public AppointmentStatus status;
	public Instant endTime;
	public Patient patient;

	public static AppointmentResponse of(Appointment appointment) {
		var patient = appointment.getPatient();

		return AppointmentResponse.builder()
			.id(appointment.getId())
			.startTime(appointment.getStartTime() != null ? appointment.getStartTime() : null)
			.endTime(appointment.getEndTime() != null ? appointment.getEndTime() : null)
			.status(appointment.getStatus())
			.patient(Patient.builder()
				.id(patient.getId())
				.fullName(patient.getFullName())
				.phoneNumber(patient.getPhoneNumber())
				.cpf(patient.getCpf())
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
