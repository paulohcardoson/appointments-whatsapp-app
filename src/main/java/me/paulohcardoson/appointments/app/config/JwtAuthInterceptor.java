package me.paulohcardoson.appointments.app.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.paulohcardoson.appointments.app.exceptions.AppError;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

	private final JWTVerifier verifier;

	public JwtAuthInterceptor(AppConfig appConfig) {
		this.verifier = JWT.require(appConfig.jwtAlgorithm).build();
	}

	@Override
	public boolean preHandle(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler
	) {
		if (Objects.equals(request.getMethod(), "OPTIONS")) {
			return true; // Allow preflight requests to pass through without authentication
		}

		String token = extractTokenFromCookie(request)
			.orElseThrow(() -> new AppError(HttpStatus.UNAUTHORIZED, "Missing or invalid authorization cookie"));

		try {
			verifier.verify(token);
		} catch (JWTVerificationException e) {
			throw new AppError(HttpStatus.UNAUTHORIZED, "invalid or expired token");
		}

		return true;
	}

	private Optional<String> extractTokenFromCookie(HttpServletRequest request) {
		System.out.println(Arrays.toString(request.getCookies()));
		if (request.getCookies() == null) return Optional.empty();

		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals("Authorization"))
			.map(Cookie::getValue)
			.findFirst();
	}
}
