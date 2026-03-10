package me.paulohcardoson.appointments.app.controllers;

import jakarta.validation.Valid;
import me.paulohcardoson.appointments.app.dto.requests.CreatePatientRequestBody;
import me.paulohcardoson.appointments.app.models.Patient;
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
	public ResponseEntity<Patient> create(@Valid @RequestBody CreatePatientRequestBody body) {
		Patient patient = patientService.create(body);
		return ResponseEntity.status(HttpStatus.CREATED).body(patient);
	}

	@GetMapping
	public ResponseEntity<Page<Patient>> listAll(@PageableDefault(size = 10, sort = "fullName") Pageable pageable) {
		return ResponseEntity.ok(patientService.listAll(pageable));
	}
}
