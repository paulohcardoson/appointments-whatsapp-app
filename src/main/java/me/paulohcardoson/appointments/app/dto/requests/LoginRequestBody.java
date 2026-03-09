package me.paulohcardoson.appointments.app.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequestBody {

	@NotBlank(message = "Email is required")
	@Email
	public String email;

	@NotBlank
	@Size(min = 8, message = "Password must be at least 8 characters long")
	public String password;

}
