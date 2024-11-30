package org.ih.appointments.exchange.controller.rest;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Map;

import org.ih.appointments.exchange.dto.FhirResponse;
import org.ih.appointments.exchange.dto.ReferralDTO;
import org.ih.appointments.exchange.service.IBundleService;
import org.ih.appointments.exchange.service.IReferralService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/api/v1/appointment")
public class AppointmentRestController {

	@Autowired
	private IReferralService referralService;

	@Autowired
	private IBundleService bundleService;

	@GetMapping(value = "/practitioners", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getPractitionars(@RequestParam Map<String, String> reqParam) {
		try {
			FhirResponse res = bundleService.getResourceType("/Practitioner", reqParam);
			if (res.getStatusCode().equals("200")) {
				System.err.println("DDD>>> " + res);
				return new ResponseEntity<>(res.getResponse(), HttpStatus.OK);
			}
			return new ResponseEntity<>(res.getResponse(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			System.err.println(e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping(value = "/available/schedule", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAvailableSchedule(@RequestParam Map<String, String> reqParam) {
		try {
			FhirResponse res = bundleService.getResourceType("/Schedule", reqParam);
			if (res.getStatusCode().equals("200")) {
				System.err.println("DDD>>> " + res);
				return new ResponseEntity<>(res.getResponse(), HttpStatus.OK);
			}
			return new ResponseEntity<>(res.getResponse(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			System.err.println(e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping(value = "/available/slot", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAvailableSlot(@RequestParam Map<String, String> reqParam) {
		if (!reqParam.containsKey("status"))
			reqParam.put("status", "free");

		try {
			FhirResponse res = bundleService.getResourceType("/Slot", reqParam);
			if (res.getStatusCode().equals("200")) {
				System.err.println("DDD>>> " + res);
				return new ResponseEntity<>(res.getResponse(), HttpStatus.OK);
			}
			return new ResponseEntity<>(res.getResponse(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			System.err.println(e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/referral")
	public ResponseEntity<?> sendServiceRequest(@RequestBody ReferralDTO referralDTO)
			throws UnsupportedEncodingException, ParseException, JSONException {

		return new ResponseEntity<>(referralService.send(referralService.generateBundle(referralDTO)), HttpStatus.OK);
	}

}
