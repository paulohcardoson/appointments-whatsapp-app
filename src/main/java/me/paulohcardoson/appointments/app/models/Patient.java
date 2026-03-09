package me.paulohcardoson.appointments.app.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patients", uniqueConstraints = {
	@UniqueConstraint(name = "unique_cpf", columnNames = "cpf")
})
public class Patient {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@NotNull
	public String fullName;

	@NotNull
	public String phoneNumber;

	@NotNull
	public String cpf;

	@OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<Appointment> appointments;

}
