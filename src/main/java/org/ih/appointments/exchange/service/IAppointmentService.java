package org.ih.appointments.exchange.service;

import org.hl7.fhir.r4.model.Appointment;
import org.ih.appointments.exchange.dto.FhirResponse;
import org.ih.appointments.exchange.dto.RequestAppointmentDTO;

public interface IAppointmentService {
	Appointment generateBundle(RequestAppointmentDTO input);

	String send(Appointment appointment);

	FhirResponse sendAppointmentToExternal(Appointment appointment, String exAppointmentFhirURL) throws Exception;

}
