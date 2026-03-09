package me.paulohcardoson.appointments.app.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppError extends RuntimeException {

	private final HttpStatus httpStatus;

	public AppError(HttpStatus httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}

}
