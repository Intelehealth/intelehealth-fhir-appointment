package org.ih.appointments.exchange.service;

import java.io.UnsupportedEncodingException;

import org.hl7.fhir.r4.model.ServiceRequest;
import org.ih.appointments.exchange.dto.FhirResponse;
import org.ih.appointments.exchange.dto.ReferralDTO;

public interface IReferralService {

	ServiceRequest generateBundle(ReferralDTO input);

	String send(ServiceRequest appointment);
	
	public FhirResponse sendReferralToExternal(ServiceRequest serviceRequest, String referralAPI) throws UnsupportedEncodingException;

}
