package me.paulohcardoson.appointments.app.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class UpdateAppointmentRequestBody {

	@NotNull(message = "Start time is required")
	public Instant startTime;

	@NotNull(message = "End time is required")
	public Instant endTime;
}
