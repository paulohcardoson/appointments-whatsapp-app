package me.paulohcardoson.appointments.app.services;

import me.paulohcardoson.appointments.app.dto.requests.CreatePatientRequestBody;
import me.paulohcardoson.appointments.app.dto.requests.UpdatePatientRequestBody;
import me.paulohcardoson.appointments.app.dto.responses.PatientResponse;
import me.paulohcardoson.appointments.app.exceptions.AppError;
import me.paulohcardoson.appointments.app.models.Appointment;
import me.paulohcardoson.appointments.app.models.Patient;
import me.paulohcardoson.appointments.app.repositories.AppointmentRepository;
import me.paulohcardoson.appointments.app.repositories.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PatientService {

	private final PatientRepository patientRepository;
	private final AppointmentRepository appointmentRepository;

	public PatientService(PatientRepository patientRepository, AppointmentRepository appointmentRepository) {
		this.patientRepository = patientRepository;
		this.appointmentRepository = appointmentRepository;
	}

	public Patient update(Long id, UpdatePatientRequestBody body) {
		var patient = patientRepository.findById(id)
			.orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Patient with id " + id + " not found."));

		patientRepository.findByCpf(body.cpf)
			.filter(existing -> !existing.getId().equals(id))
			.ifPresent(existing -> { throw new AppError(HttpStatus.BAD_REQUEST, "CPF " + body.cpf + " is already in use."); });

		patient.update(body.fullName, body.phoneNumber, body.cpf);
		return patientRepository.save(patient);
	}

	public void delete(Long id) {
		if (!patientRepository.existsById(id)) {
			throw new AppError(HttpStatus.NOT_FOUND, "Patient with id " + id + " not found.");
		}
		patientRepository.deleteById(id);
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

	public Page<PatientResponse> listAll(Pageable pageable, String filter) {
		var patientsPage = (filter == null || filter.isBlank())
			? patientRepository.findAll(pageable)
			: patientRepository.findAllByFilter(filter, pageable);

		if (patientsPage.isEmpty()) {
			return Page.empty(pageable);
		}

		List<Long> patientIds = patientsPage.stream().map(Patient::getId).toList();
		Instant now = Instant.now();

		Map<Long, Appointment> lastAppointments = appointmentRepository
			.findLastAppointmentsByPatientIds(patientIds, now)
			.stream()
			.collect(Collectors.toMap(appointment -> appointment.getPatient().getId(), Function.identity(), (existing, replacement) -> existing));

		Map<Long, Appointment> nextAppointments = appointmentRepository
			.findNextAppointmentsByPatientIds(patientIds, now)
			.stream()
			.collect(Collectors.toMap(appointment -> appointment.getPatient().getId(), Function.identity(), (existing, replacement) -> existing));

		return patientsPage.map(patient -> PatientResponse.of(
			patient,
			lastAppointments.get(patient.getId()),
			nextAppointments.get(patient.getId())
		));
	}
}
