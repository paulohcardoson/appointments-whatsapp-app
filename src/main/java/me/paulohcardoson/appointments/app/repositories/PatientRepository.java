package me.paulohcardoson.appointments.app.repositories;

import me.paulohcardoson.appointments.app.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
	Optional<Patient> findByCpf(String cpf);
}
