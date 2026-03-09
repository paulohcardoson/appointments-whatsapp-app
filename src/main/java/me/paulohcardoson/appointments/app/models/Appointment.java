package me.paulohcardoson.appointments.app.models;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity(name = "appointments")
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	public Timestamp startTime;
	public Timestamp endTime;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "patient_id", nullable = false)
	public Patient patient;

}
