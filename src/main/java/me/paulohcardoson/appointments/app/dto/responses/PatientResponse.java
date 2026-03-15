package me.paulohcardoson.appointments.app.dto.responses;

import lombok.Builder;
import me.paulohcardoson.appointments.app.models.Patient;

@Builder
public class PatientResponse {
	public Long id;
	public String fullName;
	public String phoneNumber;
	public String cpf;

	public static PatientResponse of(Patient patient) {
		return PatientResponse.builder()
			.id(patient.id)
			.fullName(patient.fullName)
			.phoneNumber(patient.phoneNumber)
			.cpf(patient.cpf)
			.build();
	}
}
