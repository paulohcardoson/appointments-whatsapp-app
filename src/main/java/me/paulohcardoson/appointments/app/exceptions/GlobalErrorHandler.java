package me.paulohcardoson.appointments.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalErrorHandler {

	@ExceptionHandler(AppError.class)
	public ResponseEntity<ErrorResponse> handleAppError(AppError ex) {
		ErrorResponse body = new ErrorResponse(
			ex.getHttpStatus().value(),
			ex.getHttpStatus().getReasonPhrase(),
			ex.getMessage(),
			Instant.now()
		);
		return ResponseEntity.status(ex.getHttpStatus()).body(body);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(HttpMessageNotReadableException ex) {
		ErrorResponse body = new ErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			HttpStatus.BAD_REQUEST.getReasonPhrase(),
			"Malformed JSON request",
			Instant.now()
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = new HashMap<>();
		for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fe.getField(), fe.getDefaultMessage());
		}

		String message = fieldErrors.toString();
		ErrorResponse body = new ErrorResponse(
			HttpStatus.UNPROCESSABLE_ENTITY.value(),
			HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
			message,
			Instant.now()
		);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException ex) {
		ErrorResponse body = new ErrorResponse(
			HttpStatus.NOT_FOUND.value(),
			HttpStatus.NOT_FOUND.getReasonPhrase(),
			"The requested resource was not found",
			Instant.now()
		);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
		System.out.println(ex);

		ErrorResponse body = new ErrorResponse(
			HttpStatus.INTERNAL_SERVER_ERROR.value(),
			HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
			"An unexpected error occurred",
			Instant.now()
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}

	public record ErrorResponse(int status, String error, String message, Instant timestamp) {
	}
}
