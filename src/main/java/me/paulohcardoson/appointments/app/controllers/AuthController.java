package me.paulohcardoson.appointments.app.controllers;

import jakarta.validation.Valid;
import me.paulohcardoson.appointments.app.dto.requests.LoginRequestBody;
import me.paulohcardoson.appointments.app.services.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@Validated
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@Valid @RequestBody LoginRequestBody body) {
		String token = authService.login(body);

		var authorizationCookie = ResponseCookie.from("Authorization", token)
			.httpOnly(true)
//			.secure(true)
			.path("/")
			.maxAge(15 * 24 * 60 * 60) // 15 days
			.build();

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, authorizationCookie.toString())
			.body(null);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		var clearedCookie = ResponseCookie.from("Authorization", "")
			.httpOnly(true)
			.path("/")
			.maxAge(0)
			.build();

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, clearedCookie.toString())
			.build();
	}

	@GetMapping("/me")
	public ResponseEntity<Void> me() {
		return ResponseEntity.ok().build();
	}
}
