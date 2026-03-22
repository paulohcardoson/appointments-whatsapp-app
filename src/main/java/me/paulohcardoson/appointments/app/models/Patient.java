package me.paulohcardoson.appointments.app.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "patients", uniqueConstraints = {
	@UniqueConstraint(name = "unique_cpf", columnNames = "cpf")
})
public class Patient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String fullName;

	@NotNull
	private String phoneNumber;

	@NotNull
	private String cpf;

	@OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Appointment> appointments;

	public void update(String fullName, String phoneNumber, String cpf) {
		this.fullName = fullName;
		this.phoneNumber = phoneNumber;
		this.cpf = cpf;
	}

}
