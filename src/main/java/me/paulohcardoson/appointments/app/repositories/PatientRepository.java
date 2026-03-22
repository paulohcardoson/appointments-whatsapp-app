package me.paulohcardoson.appointments.app.repositories;

import me.paulohcardoson.appointments.app.models.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
	Optional<Patient> findByCpf(String cpf);

	@Query("""
		SELECT p FROM Patient p
		WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :filter, '%'))
		OR LOWER(p.cpf) LIKE LOWER(CONCAT('%', :filter, '%'))
		OR LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :filter, '%'))
		""")
	Page<Patient> findAllByFilter(@Param("filter") String filter, Pageable pageable);
}
