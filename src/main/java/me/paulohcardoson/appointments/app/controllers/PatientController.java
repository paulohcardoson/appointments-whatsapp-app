package me.paulohcardoson.appointments.app.controllers;

import jakarta.validation.Valid;
import me.paulohcardoson.appointments.app.dto.requests.CreatePatientRequestBody;
import me.paulohcardoson.appointments.app.dto.requests.UpdatePatientRequestBody;
import me.paulohcardoson.appointments.app.dto.responses.PatientResponse;
import me.paulohcardoson.appointments.app.services.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
public class PatientController {

	private final PatientService patientService;

	public PatientController(PatientService patientService) {
		this.patientService = patientService;
	}

	@PostMapping("/create")
	public ResponseEntity<PatientResponse> create(@Valid @RequestBody CreatePatientRequestBody body) {
		var patient = PatientResponse.of(patientService.create(body));

		return ResponseEntity.status(HttpStatus.CREATED).body(patient);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PatientResponse> update(
		@PathVariable Long id,
		@Valid @RequestBody UpdatePatientRequestBody body
	) {
		return ResponseEntity.ok(PatientResponse.of(patientService.update(id, body)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		patientService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<Page<PatientResponse>> listAll(
		@PageableDefault(size = 10, sort = "fullName") Pageable pageable,
		@RequestParam(required = false) String filter
	) {
		return ResponseEntity.ok(patientService.listAll(pageable, filter));
	}
}
