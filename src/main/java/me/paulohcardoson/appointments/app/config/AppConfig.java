package me.paulohcardoson.appointments.app.config;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	public Algorithm jwtAlgorithm;
	@Value("${jwt.secret}")
	public String jwtSecret;
	public int jwtExpirationInHours;

	public AppConfig(
		@Value("${jwt.secret}") String jwtSecret,
		@Value("${jwt.expirationInHours}") int jwtExpirationInHours
	) {
		this.jwtSecret = jwtSecret;
		this.jwtExpirationInHours = jwtExpirationInHours;
		this.jwtAlgorithm = Algorithm.HMAC256(jwtSecret);
	}
}
