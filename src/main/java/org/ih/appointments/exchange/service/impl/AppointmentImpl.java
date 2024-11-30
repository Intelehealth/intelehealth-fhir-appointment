package org.ih.appointments.exchange.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Appointment;
import org.hl7.fhir.r4.model.Appointment.AppointmentParticipantComponent;
import org.hl7.fhir.r4.model.Appointment.AppointmentStatus;
import org.hl7.fhir.r4.model.Appointment.ParticipantRequired;
import org.hl7.fhir.r4.model.Appointment.ParticipationStatus;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Reference;
import org.ih.appointments.exchange.dto.FhirResponse;
import org.ih.appointments.exchange.dto.RequestAppointmentDTO;
import org.ih.appointments.exchange.model.DataExchangeAuditLog;
import org.ih.appointments.exchange.service.IAppointmentService;
import org.ih.appointments.exchange.service.IDataExchangeAuditLogService;
import org.ih.appointments.exchange.utils.DateUtils;
import org.ih.appointments.exchange.utils.HttpWebClient;
import org.ih.appointments.exchange.utils.IHConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;

@Service
public class AppointmentImpl extends IHConstant implements IAppointmentService {
	FhirContext fhirContext = FhirContext.forR4();

	@Autowired
	private IDataExchangeAuditLogService dataExchangeService;

	@Override
	public Appointment generateBundle(RequestAppointmentDTO dto) {
		Appointment appointment = new Appointment();
		appointment.setId(dto.getRequestId());
		appointment.setStatus(AppointmentStatus.BOOKED);

		List<CodeableConcept> theServiceCategory = generateCode("gp", "http://example.org/service-category",
				"General Practice");
		appointment.setServiceCategory(theServiceCategory);

		List<CodeableConcept> theServiceType = generateCode("52", "", "General Discussion");
		appointment.setServiceType(theServiceType);

		List<CodeableConcept> theSpecialty = generateCode(dto.getSpecialty(), "http://snomed.info/sct",
				dto.getSpecialty());
		appointment.setSpecialty(theSpecialty);

		List<CodeableConcept> theAppointmentType = generateCode("FOLLOWUP",
				"http://terminology.hl7.org/CodeSystem/v2-0276", "A follow up visit from a previous appointment");
		appointment.setAppointmentType(theAppointmentType.get(0));

		appointment.setMinutesDuration(dto.getDuration());
		List<AppointmentParticipantComponent> theParticipant = new ArrayList<Appointment.AppointmentParticipantComponent>();

		String patientReference = crFhirURL + "/Patient/" + dto.getPatientId();
		AppointmentParticipantComponent theParticipantPatient = generateParticipant(patientReference,
				dto.getPatientName(), "Patient", "", "");
		theParticipant.add(theParticipantPatient);

		AppointmentParticipantComponent theParticipantLocation = generateParticipant("Location/" + dto.getLocation(),
				dto.getLocationName(), "Location", "", "");

		theParticipant.add(theParticipantLocation);
		AppointmentParticipantComponent theParticipantPractitioner = generateParticipant(
				"Practitioner/" + dto.getPractitioner(), dto.getPractitionerName(), "Practitioner",
				"http://terminology.hl7.org/CodeSystem/v3-ParticipationType", "ATND");

		theParticipant.add(theParticipantPractitioner);

		/*
		 * firFhirConfig.getOpenCRFhirContext().transaction()
		 * .withBundle(transactionBundle).execute();
		 */

		List<Reference> slots = new ArrayList<Reference>();
		Reference re = new Reference();
		re.setReference("Slot/" + dto.getSlot());

		slots.add(re);

		appointment.setSlot(slots);
		appointment.setParticipant(theParticipant);

		return appointment;
	}

	@Override
	public String send(Appointment appointment) {
		Bundle transactionBundle = new Bundle();
		Bundle.BundleEntryComponent component = transactionBundle.addEntry();

		component.setResource(appointment);
		component.getRequest().setUrl(appointment.fhirType() + "/" + appointment.getIdElement().getIdPart())
				.setMethod(Bundle.HTTPVerb.PUT);
		transactionBundle.setType(Bundle.BundleType.TRANSACTION);
		System.err.println("DDD>>>>>>>>"
				+ fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(transactionBundle));

		return null;
	}

	@Override
	public FhirResponse sendAppointmentToExternal(Appointment appointment, String exAppointmentFhirURL)
			throws UnsupportedEncodingException {
		Bundle transactionBundle = new Bundle();
		Bundle.BundleEntryComponent component = transactionBundle.addEntry();
		String idPart = appointment.getIdElement().getIdPart();
		component.setResource(appointment);
		component.getRequest().setUrl(appointment.fhirType() + "/" + idPart).setMethod(Bundle.HTTPVerb.PUT);
		transactionBundle.setType(Bundle.BundleType.TRANSACTION);

		String payload = fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(transactionBundle);

		System.err.println("DDD>>>>>>>> " + payload);

		DataExchangeAuditLog log = new DataExchangeAuditLog();
		log.setResourceName("Appointment");
		log.setResourceUuid(idPart);
		log.setRequest(payload);
		log.setRequestUrl(exAppointmentFhirURL + "/");
		DataExchangeAuditLog uLog = dataExchangeService.save(log);

		String username = opencrOpenhimAuthentication.split(":")[0];
		String password = opencrOpenhimAuthentication.split(":")[1];
		
		FhirResponse res = HttpWebClient.postWithBasicAuth(exAppointmentFhirURL, "/",username, password, payload);

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

	private List<CodeableConcept> generateCode(String codeValue, String systemValue, String displayValue) {
		List<CodeableConcept> theServiceCategory = new ArrayList<CodeableConcept>();
		CodeableConcept cc = new CodeableConcept();
		List<Coding> theCoding = new ArrayList<Coding>();
		Coding code = new Coding();
		code.setCode(codeValue);
		if (!StringUtils.isBlank(systemValue)) {
			code.setSystem(systemValue);
		}
		code.setDisplay(displayValue);
		theCoding.add(code);
		cc.setCoding(theCoding);
		theServiceCategory.add(cc);
		return theServiceCategory;
	}

	private AppointmentParticipantComponent generateParticipant(String reference, String displayValue, String type,
			String system, String code) {

		AppointmentParticipantComponent apc = new AppointmentParticipantComponent();
		apc.setStatus(ParticipationStatus.ACCEPTED);
		apc.setRequired(ParticipantRequired.REQUIRED);

		Reference value = new Reference();
		value.setReference(reference);
		value.setDisplay(displayValue);
		apc.setActor(value);

		if (type.equalsIgnoreCase("Practitioner")) {

			List<CodeableConcept> theType = generateCode(code, system, "");
			apc.setType(theType);
		}
		// theParticipant.add(apc);
		return apc;
	}

	private String extractResponseId(Bundle bundle) {
		if (bundle.getEntry().size() != 1)
			return null;
		BundleEntryResponseComponent response = bundle.getEntryFirstRep().getResponse();
		return response.getLocation().split("/")[1];
	}
}
