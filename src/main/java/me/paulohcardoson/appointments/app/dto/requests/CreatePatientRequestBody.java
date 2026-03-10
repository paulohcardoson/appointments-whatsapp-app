package me.paulohcardoson.appointments.app.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreatePatientRequestBody {

	@NotBlank(message = "Full name is required")
	public String fullName;

	@NotBlank(message = "Phone is required")
	public String phoneNumber;

	@NotBlank(message = "CPF is required")
	@Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "CPF must be in the format XXX.XXX.XXX-XX")
	public String cpf;
	
}
