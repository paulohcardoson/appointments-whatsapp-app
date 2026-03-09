package me.paulohcardoson.appointments.app.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class CreateAppointmentRequestBody {

	@NotNull
	public Long patientId;

	@NotNull(message = "Start time is required")
	public Timestamp startTime;

	@NotNull(message = "End time is required")
	public Timestamp endTime;
}
