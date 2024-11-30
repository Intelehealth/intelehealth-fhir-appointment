package org.ih.appointments.exchange.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestIntent;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestStatus;
import org.ih.appointments.exchange.dto.FhirResponse;
import org.ih.appointments.exchange.dto.ReferralDTO;
import org.ih.appointments.exchange.model.DataExchangeAuditLog;
import org.ih.appointments.exchange.service.IDataExchangeAuditLogService;
import org.ih.appointments.exchange.service.IReferralService;
import org.ih.appointments.exchange.utils.DateUtils;
import org.ih.appointments.exchange.utils.HttpWebClient;
import org.ih.appointments.exchange.utils.IHConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;

@Service
public class ReferralServiceImpl extends IHConstant implements IReferralService {

	FhirContext fhirContext = FhirContext.forR4();

	@Autowired
	private IDataExchangeAuditLogService dataExchangeService;

	@Override
	public ServiceRequest generateBundle(ReferralDTO dto) {
		ServiceRequest serviceRequest = new ServiceRequest();
		serviceRequest.setId(UUID.randomUUID().toString());
		String patientReference = crFhirURL + "/Patient/" + dto.getPatientId();

		Reference thePatient = new Reference();
		thePatient.setType("Patient");
		thePatient.setReference(patientReference);
		thePatient.setDisplay(dto.getPatientName());
		serviceRequest.setSubject(thePatient);

		String encounterReference = crFhirURL + "/Encounter/" + dto.getEncounterId();
		Reference theEncounter = new Reference();
		theEncounter.setType("Encounter");
		theEncounter.setReference(encounterReference);
		serviceRequest.setEncounter(theEncounter);

		String practitionerReference = crFhirURL + "/Practitioner/" + dto.getPractitioner();
		Reference thePractitioner = new Reference();
		thePractitioner.setType("Practitioner");
		thePractitioner.setReference(practitionerReference);
		thePractitioner.setDisplay(dto.getPractitionerName());
		serviceRequest.setRequester(thePractitioner);

		serviceRequest.setStatus(ServiceRequestStatus.ACTIVE);
		serviceRequest.setIntent(ServiceRequestIntent.PROPOSAL);

		List<CodeableConcept> theReasonCode = new ArrayList<CodeableConcept>();
		CodeableConcept codeableConcept = new CodeableConcept();
		codeableConcept.setText(dto.getReason());
		theReasonCode.add(codeableConcept);
		serviceRequest.setReasonCode(theReasonCode);

		return serviceRequest;
	}

	@Override
	public String send(ServiceRequest serviceRequest) {
		Bundle transactionBundle = new Bundle();
		Bundle.BundleEntryComponent component = transactionBundle.addEntry();

		component.setResource(serviceRequest);
		component.getRequest().setUrl(serviceRequest.fhirType() + "/" + serviceRequest.getIdElement().getIdPart())
				.setMethod(Bundle.HTTPVerb.PUT);
		transactionBundle.setType(Bundle.BundleType.TRANSACTION);
		System.err.println("DDD>>>>>>>>"
				+ fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(transactionBundle));
		return null;
	}

	public FhirResponse sendReferralToExternal(ServiceRequest serviceRequest, String referralAPI)
			throws UnsupportedEncodingException {
		Bundle transactionBundle = new Bundle();
		Bundle.BundleEntryComponent component = transactionBundle.addEntry();
		String idPart = serviceRequest.getIdElement().getIdPart();
		component.setResource(serviceRequest);
		component.getRequest().setUrl(serviceRequest.fhirType() + "/" + idPart).setMethod(Bundle.HTTPVerb.PUT);
		transactionBundle.setType(Bundle.BundleType.TRANSACTION);
		String payload = fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(transactionBundle);
		System.err.println("DDD>>>>>>>>" + payload);

		DataExchangeAuditLog log = new DataExchangeAuditLog();
		log.setResourceName("Referral");
		log.setResourceUuid(idPart);
		log.setRequest(payload);
		log.setRequestUrl(referralAPI + "/");
		DataExchangeAuditLog uLog = dataExchangeService.save(log);

		String username = opencrOpenhimAuthentication.split(":")[0];
		String password = opencrOpenhimAuthentication.split(":")[1];
		FhirResponse res = HttpWebClient.postWithBasicAuth(referralAPI, "/", username, password, payload);

		System.err.println("DDD >>>>> " + res);

		uLog.setResponse(res.getResponse());
		uLog.setResponseStatus(res.getStatusCode());

		if (res.getStatusCode().equals("200")) {
			Bundle remoteBundle = fhirContext.newJsonParser().parseResource(Bundle.class, res.getResponse());
			uLog.setFhirId(extractResponseId(remoteBundle));
			uLog.setStatus(true);
		} else {
			uLog.setStatus(false);
		}
		uLog.setChangedBy(1); // Admin-OpenMRS
		uLog.setDateChanged(DateUtils.toFormattedDateNow());
		dataExchangeService.update(uLog);
		return res;
	}

	private String extractResponseId(Bundle bundle) {
		if (bundle.getEntry().size() != 1)
			return null;
		BundleEntryResponseComponent response = bundle.getEntryFirstRep().getResponse();
		return response.getLocation().split("/")[1];
	}

}
