package me.paulohcardoson.appointments.app.config;

import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

	public final String whatsappFrom;

	public TwilioConfig(
		@Value("${twilio.account.sid}") String accountSid,
		@Value("${twilio.account.auth-token}") String authToken,
		@Value("${twilio.whatsapp.from}") String whatsappFrom
	) {
		Twilio.init(accountSid, authToken);
		this.whatsappFrom = whatsappFrom;
	}
}
