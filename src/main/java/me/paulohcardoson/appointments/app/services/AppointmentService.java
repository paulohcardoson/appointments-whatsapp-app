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

	public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository) {
		this.appointmentRepository = appointmentRepository;
		this.patientRepository = patientRepository;
	}

	public Appointment create(CreateAppointmentRequestBody data) {
		var patient = patientRepository.findById(data.patientId)
			.orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Patient with id " + data.patientId + " not found."));

		Appointment appointment = Appointment.builder()
			.startTime(data.startTime)
			.endTime(data.endTime)
			.patient(patient)
			.build();

		return appointmentRepository.save(appointment);
	}

	public Page<Appointment> listAll(Pageable pageable) {
		return appointmentRepository.findAll(pageable);
	}
}
