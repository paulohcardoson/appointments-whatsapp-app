package me.paulohcardoson.appointments.app.services;

import me.paulohcardoson.appointments.app.dto.requests.CreateAppointmentRequestBody;
import me.paulohcardoson.appointments.app.enums.AppointmentStatus;
import me.paulohcardoson.appointments.app.exceptions.AppError;
import me.paulohcardoson.appointments.app.models.Appointment;
import me.paulohcardoson.appointments.app.repositories.AppointmentRepository;
import me.paulohcardoson.appointments.app.repositories.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;



@Service
public class AppointmentService {

	private static final String TOKEN_PREFIX = "appointment:confirm:";

	private final AppointmentRepository appointmentRepository;
	private final PatientRepository patientRepository;
	private final WhatsAppService whatsAppService;
	private final RedisTemplate<String, Object> redisTemplate;

	public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository, WhatsAppService whatsAppService, RedisTemplate<String, Object> redisTemplate) {
		this.appointmentRepository = appointmentRepository;
		this.patientRepository = patientRepository;
		this.whatsAppService = whatsAppService;
		this.redisTemplate = redisTemplate;
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

	public Appointment confirmByToken(String token) {
		String key = TOKEN_PREFIX + token;
		Number value = (Number) redisTemplate.opsForValue().get(key);

		if (value == null) {
			throw new AppError(HttpStatus.NOT_FOUND, "Invalid or expired confirmation token.");
		}

		var appointment = appointmentRepository.findById(value.longValue())
			.orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "Appointment not found."));

		if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
			throw new AppError(HttpStatus.CONFLICT, "Appointment is already confirmed.");
		}

		appointment.confirm();
		appointmentRepository.save(appointment);
		redisTemplate.delete(key);

		return appointment;
	}

	public Page<Appointment> listAll(Pageable pageable) {
		return appointmentRepository.findAllWithPatient(pageable);
	}
}
