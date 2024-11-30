package org.ih.appointments.exchange.exp;

public class NoAppointmentFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoAppointmentFoundException(String msg) {
		super(msg);
	}

}
