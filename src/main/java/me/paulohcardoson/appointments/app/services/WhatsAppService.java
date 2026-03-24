package me.paulohcardoson.appointments.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import me.paulohcardoson.appointments.app.config.TwilioConfig;
import me.paulohcardoson.appointments.app.models.Appointment;
import me.paulohcardoson.appointments.app.models.Patient;
import me.paulohcardoson.appointments.app.repositories.AppointmentRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class WhatsAppService {

	private static final String APP_URL = "http://localhost:5173";
	private static final String TOKEN_PREFIX = "appointment:confirm:";
	private static final Duration TOKEN_TTL = Duration.ofHours(24);

	private static final DateTimeFormatter DATE_FORMATTER =
		DateTimeFormatter.ofPattern("dd/MM").withZone(ZoneId.of("America/Manaus"));

	private static final DateTimeFormatter TIME_FORMATTER =
		DateTimeFormatter.ofPattern("HH'h'mm").withZone(ZoneId.of("America/Manaus"));

	private final TwilioConfig twilioConfig;
	private final RedisTemplate<String, Object> redisTemplate;
	private final AppointmentRepository appointmentRepository;

	public WhatsAppService(TwilioConfig twilioConfig, RedisTemplate<String, Object> redisTemplate, AppointmentRepository appointmentRepository) {
		this.twilioConfig = twilioConfig;
		this.redisTemplate = redisTemplate;
		this.appointmentRepository = appointmentRepository;
	}

	public void sendAppointmentConfirmationMessage(Appointment appointment) {
		String phoneNumber = appointment.getPatient().getPhoneNumber();
		String to = "whatsapp:" + phoneNumber;

		String token = UUID.randomUUID().toString();
		redisTemplate.opsForValue().set(TOKEN_PREFIX + token, appointment.getId(), TOKEN_TTL);

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode variables = mapper.createObjectNode();
		variables.put("name", appointment.getPatient().getFullName());
		variables.put("date", DATE_FORMATTER.format(appointment.getStartTime()));
		variables.put("time", TIME_FORMATTER.format(appointment.getStartTime()));
		variables.put("token", token);

		Message.creator(
				new PhoneNumber(to),
				new PhoneNumber(twilioConfig.whatsappFrom),
				(String) null
			)
			.setContentSid("HXf7b0ef806f621695fe6cb13e390df266")
			.setContentVariables(variables.toString())
			.create();

		appointment.markAsWaitingForConfirmation();
		appointmentRepository.save(appointment);
	}

	public void sendAppointmentConfirmation(Appointment appointment) {
		String phoneNumber = appointment.getPatient().getPhoneNumber();
		String to = "whatsapp:" + phoneNumber;

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode variables = mapper.createObjectNode();
		variables.put("name", appointment.getPatient().getFullName());
		variables.put("date", DATE_FORMATTER.format(appointment.getStartTime()));
		variables.put("time", TIME_FORMATTER.format(appointment.getStartTime()));

		Message.creator(
				new PhoneNumber(to),
				new PhoneNumber(twilioConfig.whatsappFrom),
				(String) null
			)
			.setContentSid("HXbfefed9a3ce86a6a9c485a53bcb11c1d")
			.setContentVariables(variables.toString())
			.create();
	}

	public void sendManualMessage(Patient patient) {
		String to = "whatsapp:" + patient.getPhoneNumber();

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode variables = mapper.createObjectNode();
		variables.put("name", patient.getFullName());

		Message.creator(
				new PhoneNumber(to),
				new PhoneNumber(twilioConfig.whatsappFrom),
				(String) null
			)
			.setContentSid("HXc72c51dd4a339c6c0c1860cd0107c49c")
			.setContentVariables(variables.toString())
			.create();
	}
}
