package me.paulohcardoson.appointments.app.services;

import com.auth0.jwt.JWT;
import me.paulohcardoson.appointments.app.config.AppConfig;
import me.paulohcardoson.appointments.app.dto.requests.LoginRequestBody;
import me.paulohcardoson.appointments.app.exceptions.AppError;
import me.paulohcardoson.appointments.app.repositories.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

	private final UsersRepository usersRepository;
	private final AppConfig appConfig;

	public AuthService(
		UsersRepository usersRepository,
		AppConfig appConfig
	) {
		this.usersRepository = usersRepository;
		this.appConfig = appConfig;
	}

	public String login(LoginRequestBody body) {
		var admin = usersRepository.findByEmail(body.getEmail())
			.orElseThrow(() -> new AppError(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

		var passwordsMatch = BCrypt.checkpw(body.password, admin.password);

		if (!passwordsMatch) {
			throw new AppError(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}

		Date now = new Date();
		Date expiresAt = new Date(now.getTime() + (appConfig.jwtExpirationInHours * 60L * 60L * 1000L));

		return JWT.create()
			.withSubject(String.valueOf(admin.id))
			.withClaim("email", admin.email)
			.withIssuedAt(now)
			.withExpiresAt(expiresAt)
			.sign(appConfig.jwtAlgorithm);
	}

}
