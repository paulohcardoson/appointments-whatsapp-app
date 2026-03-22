package me.paulohcardoson.appointments.app.controllers;

import me.paulohcardoson.appointments.app.dto.responses.StatsResponse;
import me.paulohcardoson.appointments.app.services.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatsController {

	private final StatsService statsService;

	public StatsController(StatsService statsService) {
		this.statsService = statsService;
	}

	@GetMapping
	public ResponseEntity<StatsResponse> getStats() {
		return ResponseEntity.ok(statsService.getStats());
	}
}
