package me.paulohcardoson.appointments.app.services;

import me.paulohcardoson.appointments.app.dto.requests.CreateAppointmentRequestBody;
import me.paulohcardoson.appointments.app.exceptions.AppError;
import me.paulohcardoson.appointments.app.models.Appointment;
import me.paulohcardoson.appointments.app.repositories.AppointmentRepository;
import me.paulohcardoson.appointments.app.repositories.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final PatientRepository patientRepository;
	private final WhatsAppService whatsAppService;

	public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository, WhatsAppService whatsAppService) {
		this.appointmentRepository = appointmentRepository;
		this.patientRepository = patientRepository;
		this.whatsAppService = whatsAppService;
	}

	public Appointment create(CreateAppointmentRequestBody data) {
		if (data.startTime.isAfter(data.endTime)) {
			throw new AppError(HttpStatus.BAD_REQUEST, "End time must be after start time.");
		}

		var patient = patientRepository.findById(data.patientId)
			.orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Patient with id " + data.patientId + " not found."));

		var appointment = Appointment.builder()
			.startTime(data.startTime)
			.endTime(data.endTime)
			.patient(patient)
			.build();

		var saved = appointmentRepository.save(appointment);
		whatsAppService.sendAppointmentConfirmation(saved);
		return saved;
	}

	public Page<Appointment> listAll(Pageable pageable) {
		return appointmentRepository.findAllWithPatient(pageable);
	}
}
