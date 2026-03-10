package me.paulohcardoson.appointments.app.repositories;

import me.paulohcardoson.appointments.app.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
