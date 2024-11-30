package org.ih.appointments.exchange.service.impl;

import java.util.Map;

import org.ih.appointments.exchange.dao.CommonOperationDao;
import org.ih.appointments.exchange.dto.FhirResponse;
import org.ih.appointments.exchange.service.IBundleService;
import org.ih.appointments.exchange.utils.HttpWebClient;
import org.ih.appointments.exchange.utils.IHConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;

@Service
public class BundleServiceImpl extends IHConstant implements IBundleService {

	FhirContext fhirContext = FhirContext.forR4();

	@Autowired
	private CommonOperationDao comOprDAO;

	@Override
	public FhirResponse getResourceType(String resourceType, Map<String, String> reqParam) {
		String locationUUID = reqParam.getOrDefault("locationUUID", "");

		if (locationUUID == null || locationUUID.isEmpty()) {
			FhirResponse res = new FhirResponse();
			res.setStatusCode("400");
			res.setResponse("Invalid Location ID");
			return res;
		}

		String appointmentURL = comOprDAO.findAppointmentServerUrlByLocationV2(locationUUID);

		if (appointmentURL == null || appointmentURL.isEmpty()) {
			FhirResponse res = new FhirResponse();
			res.setStatusCode("400");
			res.setResponse("Couldn't Resolve The Appointment Facility Due to Invalid Location ID");
			return res;
		}

		reqParam.remove("locationUUID");

		System.err.println("Appointment URL: " + appointmentURL);

		String username = opencrOpenhimAuthentication.split(":")[0];
		String password = opencrOpenhimAuthentication.split(":")[1];

		FhirResponse response = HttpWebClient.get(appointmentURL, resourceType, reqParam, username, password);
		return response;
	}

}
