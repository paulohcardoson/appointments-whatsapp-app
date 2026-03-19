package me.paulohcardoson.appointments.app.dto.responses;

import lombok.Builder;
import me.paulohcardoson.appointments.app.models.Appointment;
import me.paulohcardoson.appointments.app.models.Patient;

import java.time.Instant;

@Builder
public class PatientResponse {
	public Long id;
	public String fullName;
	public String phoneNumber;
	public String cpf;
	public AppointmentSummary lastAppointment;
	public AppointmentSummary nextAppointment;

	public static PatientResponse of(Patient patient) {
		return of(patient, null, null);
	}

	public static PatientResponse of(Patient patient, Appointment lastAppointment, Appointment nextAppointment) {
		return PatientResponse.builder()
			.id(patient.getId())
			.fullName(patient.getFullName())
			.phoneNumber(patient.getPhoneNumber())
			.cpf(patient.getCpf())
			.lastAppointment(AppointmentSummary.fromAppointment(lastAppointment))
			.nextAppointment(AppointmentSummary.fromAppointment(nextAppointment))
			.build();
	}

	@Builder
	public static class AppointmentSummary {
		public Long id;
		public Instant startTime;
		public Instant endTime;

		public static AppointmentSummary fromAppointment(Appointment appointment) {
			if (appointment == null) return null;

			return AppointmentSummary.builder()
				.id(appointment.getId())
				.startTime(appointment.getStartTime() != null ? appointment.getStartTime() : null)
				.endTime(appointment.getEndTime() != null ? appointment.getEndTime() : null)
				.build();
		}
	}
}
