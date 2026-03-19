package me.paulohcardoson.appointments.app.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreatePatientRequestBody {

	@NotBlank(message = "Full name is required")
	public String fullName;

	@NotBlank(message = "Phone is required")
	@Pattern(regexp = "^[+]\\d{13}", message = "Phone number must be in the format +1234567890123")
	public String phoneNumber;

	@NotBlank(message = "CPF is required")
	@Pattern(regexp = "^\\d{11}", message = "CPF must be 11 digits")
	public String cpf;

}
