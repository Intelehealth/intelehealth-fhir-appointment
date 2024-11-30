package org.ih.appointments.exchange.service;

import java.util.Map;

import org.ih.appointments.exchange.dto.FhirResponse;

public interface IBundleService {

//	public String getBundleByResourceType(String resourceType, Map<String, String> reqParam);

	public FhirResponse getResourceType(String resourceType, Map<String, String> reqParam);

}
