package me.paulohcardoson.appointments.app.enums;

public enum PatientOrderBy {
	NAME("fullName"),
	CPF("cpf"),
	PHONE_NUMBER("phoneNumber");

	public final String field;

	PatientOrderBy(String field) {
		this.field = field;
	}
}
