package org.ih.appointments.exchange.scheduler;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;

import org.hl7.fhir.r4.model.Appointment;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.ih.appointments.exchange.dao.CommonOperationDao;
import org.ih.appointments.exchange.dao.ConfigDataSyncDao;
import org.ih.appointments.exchange.dao.ExternalAppointmentDao;
import org.ih.appointments.exchange.dao.ReferralDao;
import org.ih.appointments.exchange.datatype.ConfigFacilityDataType;
import org.ih.appointments.exchange.dto.ConfigDataSync;
import org.ih.appointments.exchange.dto.FhirResponse;
import org.ih.appointments.exchange.dto.ReferralDTO;
import org.ih.appointments.exchange.dto.RequestAppointmentDTO;
import org.ih.appointments.exchange.exp.NoAppointmentFoundException;
import org.ih.appointments.exchange.model.IHMarker;
import org.ih.appointments.exchange.service.IAppointmentService;
import org.ih.appointments.exchange.service.IReferralService;
import org.ih.appointments.exchange.service.impl.IHMarkerService;
import org.ih.appointments.exchange.utils.IHConstant;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import ca.uhn.fhir.parser.DataFormatException;

@Component
public class DataSendToFHIR extends IHConstant {

	@Autowired
	private ExternalAppointmentDao externalAppointmentDao;

	@Autowired
	private ReferralDao referralDao;

	@Autowired
	private IAppointmentService iAppointmentService;

	@Autowired
	private IReferralService referralService;

	@Autowired
	private IHMarkerService ihMarkerService;

	@Autowired
	private CommonOperationDao commonOprDao;

	@Autowired
	private ConfigDataSyncDao configDataSyncDao;

	@Scheduled(fixedDelay = 60000, initialDelay = 60000)
	public void scheduleTaskUsingCronExpression() throws ParseException, UnsupportedEncodingException,
			DataFormatException, JsonProcessingException, JSONException {

		ConfigDataSync appointmentDataSync = configDataSyncDao.getConfigDataSync(ConfigFacilityDataType.APPOINTMENT);

		if (appointmentDataSync.getStatus()) {
			transferAppointment();
		} else {
			System.err.println("Appointment sync is disabled");
		}

		ConfigDataSync referralDataSync = configDataSyncDao.getConfigDataSync(ConfigFacilityDataType.REFERRAL);

		if (referralDataSync.getStatus()) {
			transferReferral();
		} else {
			System.err.println("Referral sync is disabled");
		}

	}

	private void transferReferral() {
		IHMarker referralMarker = ihMarkerService.findByName(exportReferral);

		List<ReferralDTO> referrals = referralDao.getReferralByDate(referralMarker.getLastSyncTime());

		System.err.println("Total Referrals Found: " + referrals.size());
		int noAppointmentError = 0;

		for (ReferralDTO dto : referrals) {
			try {
				sendReferralToExternal(dto);
			} catch (NoAppointmentFoundException e) {
				System.err.println(e);
//				noAppointmentError++;
			} catch (UnsupportedEncodingException e) {
				System.err.println(e);

			}
		}

		if (referrals.size() > 0 && noAppointmentError == 0) {
			ihMarkerService.updateMarkerByName(exportReferral);
		}
	}

	private void transferAppointment() {
		IHMarker appointmentMarker = ihMarkerService.findByName(exportAppointment);

		List<RequestAppointmentDTO> appointments = externalAppointmentDao
				.getExternalAppointmentByDate(appointmentMarker.getLastSyncTime());

		System.err.println("Total Appointments Found: " + appointments.size());

		for (RequestAppointmentDTO dto : appointments) {

			System.err.println("Sending appointment dto >> " + dto);

			try {
				sendAppointmentToExternal(dto);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(e);
			}
		}

		if (appointments.size() > 0) {
			ihMarkerService.updateMarkerByName(exportAppointment);
		}
	}

	private void sendAppointmentToExternal(RequestAppointmentDTO appDto) throws Exception {

		Appointment appointment = iAppointmentService.generateBundle(appDto);

		String externalAppointmentApi = commonOprDao.findAppointmentServerUrlByLocationV2(appDto.getLocation());

		FhirResponse res = iAppointmentService.sendAppointmentToExternal(appointment, externalAppointmentApi);
	}

	private void sendReferralToExternal(ReferralDTO dto)
			throws NoAppointmentFoundException, UnsupportedEncodingException {
		System.err.println("DDD>>>" + dto);

		RequestAppointmentDTO reqAppDTO = externalAppointmentDao
				.getExternalAppointmentByDateAndPatientId(dto.getCreated(), dto.getPatientId());

		if (reqAppDTO == null) {
			System.err.println("No appoinment found for referral >> " + dto);
			throw new NoAppointmentFoundException("No appoinment found for referral");
		}

		String externalAPI = commonOprDao.findAppointmentServerUrlByLocationV2(reqAppDTO.getLocation());

		ServiceRequest serviceReferral = referralService.generateBundle(dto);

		FhirResponse res = referralService.sendReferralToExternal(serviceReferral, externalAPI);
	}

}
