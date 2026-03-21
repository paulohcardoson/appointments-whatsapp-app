package me.paulohcardoson.appointments.app.repositories;

import me.paulohcardoson.appointments.app.models.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
	@Query("""
			SELECT a FROM Appointment a
			WHERE a.patient.id IN :patientIds
			AND a.startTime = (
			  SELECT MAX(a2.startTime) FROM Appointment a2
			  WHERE a2.patient.id = a.patient.id
			  AND a2.startTime <= :now
			)
		""")
	List<Appointment> findLastAppointmentsByPatientIds(@Param("patientIds") List<Long> patientIds, @Param("now") Instant now);

	@Query("""
			SELECT a FROM Appointment a
			WHERE a.patient.id IN :patientIds
			AND a.startTime = (
			  SELECT MIN(a2.startTime) FROM Appointment a2
			  WHERE a2.patient.id = a.patient.id
			  AND a2.startTime >= :now
			)
		""")
	List<Appointment> findNextAppointmentsByPatientIds(@Param("patientIds") List<Long> patientIds, @Param("now") Instant now);

	@Query(
		value = "SELECT a FROM Appointment a JOIN FETCH a.patient",
		countQuery = "SELECT COUNT(a) FROM Appointment a"
	)
	Page<Appointment> findAllWithPatient(Pageable pageable);

	@Query("SELECT a FROM Appointment a JOIN FETCH a.patient WHERE a.startTime >= :startOfDay AND a.startTime < :endOfDay")
	List<Appointment> findAllByDay(@Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay);

	@Query("SELECT a FROM Appointment a JOIN FETCH a.patient WHERE a.status = 'NOT_CONFIRMED' AND a.startTime >= :startOfDay AND a.startTime < :endOfDay")
	List<Appointment> findNotConfirmedByDay(@Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay);
}
