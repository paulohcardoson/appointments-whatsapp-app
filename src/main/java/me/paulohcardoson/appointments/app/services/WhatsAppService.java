package me.paulohcardoson.appointments.app.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import me.paulohcardoson.appointments.app.config.TwilioConfig;
import me.paulohcardoson.appointments.app.models.Appointment;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class WhatsAppService {

	private static final DateTimeFormatter FORMATTER =
		DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm").withZone(ZoneId.of("America/Sao_Paulo"));

	private final TwilioConfig twilioConfig;

	public WhatsAppService(TwilioConfig twilioConfig) {
		this.twilioConfig = twilioConfig;
	}

	public void sendAppointmentConfirmation(Appointment appointment) {
		var patient = appointment.getPatient();
		var to = "whatsapp:" + patient.getPhoneNumber();
		var body = String.format(
			"Olá, %s! Sua consulta foi agendada para %s. Até lá!",
			patient.getFullName(),
			FORMATTER.format(appointment.getStartTime())
		);

		Message.creator(
			new PhoneNumber(to),
			new PhoneNumber(twilioConfig.whatsappFrom),
			body
		).create();
	}
}
