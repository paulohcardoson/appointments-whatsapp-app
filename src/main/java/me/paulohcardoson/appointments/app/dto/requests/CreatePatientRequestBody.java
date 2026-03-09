package me.paulohcardoson.appointments.app.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePatientRequestBody {

	@NotBlank(message = "Full name is required")
	public String fullName;

	@NotBlank(message = "Phone is required")
	public String phoneNumber;

	@NotBlank(message = "CPF is required")
	public String cpf;
}
