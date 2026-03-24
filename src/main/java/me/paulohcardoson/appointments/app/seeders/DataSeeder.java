package me.paulohcardoson.appointments.app.seeders;

import com.github.javafaker.Faker;
import me.paulohcardoson.appointments.app.enums.AppointmentStatus;
import me.paulohcardoson.appointments.app.models.Appointment;
import me.paulohcardoson.appointments.app.models.Patient;
import me.paulohcardoson.appointments.app.repositories.AppointmentRepository;
import me.paulohcardoson.appointments.app.repositories.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

	private static final int PATIENT_COUNT = 40;

	private final PatientRepository patientRepository;
	private final AppointmentRepository appointmentRepository;

	public DataSeeder(PatientRepository patientRepository, AppointmentRepository appointmentRepository) {
		this.patientRepository = patientRepository;
		this.appointmentRepository = appointmentRepository;
	}

	@Override
	public void run(String... args) {
		if (patientRepository.count() > 0) {
			System.out.println("[DataSeeder] Database already has data, skipping seed.");
			return;
		}

		System.out.println("[DataSeeder] Seeding database...");

		Faker faker = new Faker(new Locale("pt-BR"), new Random(42));
		Instant now = Instant.now();
		List<Appointment> appointments = new ArrayList<>();

		for (int i = 0; i < PATIENT_COUNT; i++) {
			String cpf = faker.number().digits(11);
			String phoneNumber = "+55" + faker.number().digits(11);

			Patient patient = patientRepository.save(
				Patient.builder()
					.fullName(faker.name().fullName())
					.cpf(cpf)
					.phoneNumber(phoneNumber)
					.build()
			);

			if (i < 10) {
				appointments.addAll(generatePastAppointments(patient, faker, now, 1 + faker.random().nextInt(3)));
			} else if (i < 20) {
				appointments.addAll(generateFutureAppointments(patient, faker, now, 1 + faker.random().nextInt(3)));
			} else if (i < 30) {
				appointments.addAll(generatePastAppointments(patient, faker, now, 1 + faker.random().nextInt(2)));
				appointments.addAll(generateFutureAppointments(patient, faker, now, 1 + faker.random().nextInt(2)));
			}
		}

		appointmentRepository.saveAll(appointments);
		System.out.println("[DataSeeder] Seeded " + PATIENT_COUNT + " patients and " + appointments.size() + " appointments.");
	}

	private List<Appointment> generatePastAppointments(Patient patient, Faker faker, Instant now, int count) {
		List<Appointment> result = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			long daysAgo = 1 + faker.random().nextInt(180);
			int hour = 8 + faker.random().nextInt(9);
			Instant start = now.truncatedTo(ChronoUnit.DAYS).minus(daysAgo, ChronoUnit.DAYS).plus(hour, ChronoUnit.HOURS);
			AppointmentStatus status = faker.random().nextBoolean() ? AppointmentStatus.CONFIRMED : AppointmentStatus.NOT_CONFIRMED;
			result.add(Appointment.builder().patient(patient).startTime(start).endTime(start.plus(1, ChronoUnit.HOURS)).status(status).build());
		}
		return result;
	}

	private List<Appointment> generateFutureAppointments(Patient patient, Faker faker, Instant now, int count) {
		List<Appointment> result = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			long daysAhead = 1 + faker.random().nextInt(60);
			int hour = 8 + faker.random().nextInt(9);
			Instant start = now.truncatedTo(ChronoUnit.DAYS).plus(daysAhead, ChronoUnit.DAYS).plus(hour, ChronoUnit.HOURS);
			result.add(Appointment.builder().patient(patient).startTime(start).endTime(start.plus(1, ChronoUnit.HOURS)).build());
		}
		return result;
	}
}
