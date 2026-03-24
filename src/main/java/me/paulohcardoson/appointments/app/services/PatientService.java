package me.paulohcardoson.appointments.app.services;

import me.paulohcardoson.appointments.app.dto.requests.CreatePatientRequestBody;
import me.paulohcardoson.appointments.app.dto.requests.UpdatePatientRequestBody;
import me.paulohcardoson.appointments.app.dto.responses.PatientResponse;
import me.paulohcardoson.appointments.app.enums.PatientOrderBy;
import me.paulohcardoson.appointments.app.exceptions.AppError;
import me.paulohcardoson.appointments.app.models.Appointment;
import me.paulohcardoson.appointments.app.models.Patient;
import me.paulohcardoson.appointments.app.repositories.AppointmentRepository;
import me.paulohcardoson.appointments.app.repositories.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
	private final WhatsAppService whatsAppService;

	public PatientService(PatientRepository patientRepository, AppointmentRepository appointmentRepository, WhatsAppService whatsAppService) {
		this.patientRepository = patientRepository;
		this.appointmentRepository = appointmentRepository;
		this.whatsAppService = whatsAppService;
	}

	public Patient update(Long id, UpdatePatientRequestBody body) {
		var patient = patientRepository.findById(id)
			.orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Patient with id " + id + " not found."));

		patientRepository.findByCpf(body.cpf)
			.filter(existing -> !existing.getId().equals(id))
			.ifPresent(existing -> {
				throw new AppError(HttpStatus.BAD_REQUEST, "CPF " + body.cpf + " is already in use.");
			});

		patient.update(body.fullName, body.phoneNumber, body.cpf);
		return patientRepository.save(patient);
	}

	public void sendMessage(Long id) {
		var patient = patientRepository.findById(id)
			.orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Patient with id " + id + " not found."));
		whatsAppService.sendManualMessage(patient);
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

	public Page<PatientResponse> listAll(Pageable pageable, String name, Instant from, Instant to, Boolean hasNextAppointment, Boolean hasLastAppointment, PatientOrderBy orderBy) {
		Instant now = Instant.now();
		Specification<Patient> spec = Specification.unrestricted();

		if (orderBy != null) {
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orderBy.field).ascending());
		}

		if (name != null && !name.isBlank()) {
			String pattern = "%" + name.toLowerCase() + "%";
			spec = spec.and((root, query, cb) -> cb.or(
				cb.like(cb.lower(root.get("fullName")), pattern),
				cb.like(cb.lower(root.get("cpf")), pattern),
				cb.like(cb.lower(root.get("phoneNumber")), pattern)
			));
		}

		if (from != null && to != null) {
			spec = spec.and((root, query, cb) -> {
				assert query != null;
				var sub = query.subquery(Integer.class);
				var appt = sub.from(Appointment.class);
				sub.select(cb.literal(1)).where(cb.and(
					cb.equal(appt.get("patient"), root),
					cb.greaterThanOrEqualTo(appt.get("startTime"), from),
					cb.lessThanOrEqualTo(appt.get("startTime"), to)
				));
				return cb.exists(sub);
			});
		}

		if (hasNextAppointment != null) {
			spec = spec.and((root, query, cb) -> {
				var sub = query.subquery(Integer.class);
				var appt = sub.from(Appointment.class);
				sub.select(cb.literal(1)).where(cb.and(
					cb.equal(appt.get("patient"), root),
					cb.greaterThanOrEqualTo(appt.get("startTime"), now)
				));

				return hasNextAppointment ? cb.exists(sub) : cb.not(cb.exists(sub));
			});
		}

		if (hasLastAppointment != null) {
			spec = spec.and((root, query, cb) -> {
				var sub = query.subquery(Integer.class);
				var appt = sub.from(Appointment.class);
				sub.select(cb.literal(1)).where(cb.and(
					cb.equal(appt.get("patient"), root),
					cb.lessThan(appt.get("startTime"), now)
				));
				return hasLastAppointment ? cb.exists(sub) : cb.not(cb.exists(sub));
			});
		}

		var patientsPage = patientRepository.findAll(spec, pageable);

		if (patientsPage.isEmpty()) {
			return Page.empty(pageable);
		}

		List<Long> patientIds = patientsPage.stream().map(Patient::getId).toList();

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
