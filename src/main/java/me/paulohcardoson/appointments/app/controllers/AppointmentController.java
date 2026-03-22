package me.paulohcardoson.appointments.app.controllers;

import jakarta.validation.Valid;
import me.paulohcardoson.appointments.app.dto.requests.CreateAppointmentRequestBody;
import me.paulohcardoson.appointments.app.dto.requests.UpdateAppointmentRequestBody;
import me.paulohcardoson.appointments.app.dto.responses.AppointmentResponse;
import me.paulohcardoson.appointments.app.services.AppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;

	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	@PostMapping("/create")
	public ResponseEntity<AppointmentResponse> create(
		@Valid @RequestBody CreateAppointmentRequestBody body
	) {
		AppointmentResponse appointment = AppointmentResponse.of(appointmentService.create(body));
		return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
	}

	@GetMapping
	public ResponseEntity<Page<AppointmentResponse>> listAll(
		@PageableDefault(size = 10, sort = "startTime") Pageable pageable,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
	) {
		return ResponseEntity.ok(appointmentService.listAll(pageable, from, to).map(AppointmentResponse::of));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		appointmentService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<AppointmentResponse> update(
		@PathVariable Long id,
		@Valid @RequestBody UpdateAppointmentRequestBody body
	) {
		return ResponseEntity.ok(AppointmentResponse.of(appointmentService.update(id, body)));
	}

	@GetMapping("/confirm")
	public ResponseEntity<AppointmentResponse> confirm(@RequestParam String token) {
		return ResponseEntity.ok(AppointmentResponse.of(appointmentService.confirmByToken(token)));
	}

}
