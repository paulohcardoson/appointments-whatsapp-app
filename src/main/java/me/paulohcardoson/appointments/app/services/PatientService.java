package me.paulohcardoson.appointments.app.services;

import me.paulohcardoson.appointments.app.dto.requests.CreatePatientRequestBody;
import me.paulohcardoson.appointments.app.exceptions.AppError;
import me.paulohcardoson.appointments.app.models.Patient;
import me.paulohcardoson.appointments.app.repositories.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

	private final PatientRepository patientRepository;

	public PatientService(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	public Patient create(CreatePatientRequestBody body) {
		var existingPatient = patientRepository.findByCpf(body.cpf);

		if (existingPatient.isPresent()) {
			throw new AppError(HttpStatus.BAD_REQUEST, "Patient with CPF " + body.cpf + " already exists.");
		}

		Patient patient = Patient.builder()
			.fullName(body.fullName)
			.phoneNumber(body.phoneNumber)
			.cpf(body.cpf)
			.build();

		return patientRepository.save(patient);
	}

	public Page<Patient> listAll(Pageable pageable) {
		return patientRepository.findAll(pageable);
	}
}
