package org.ih.appointments.exchange.controller.rest;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import org.ih.appointments.exchange.dao.ConfigDataSyncDao;
import org.ih.appointments.exchange.datatype.ConfigFacilityDataType;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointment/api/v1/control")
public class ActivityController {

	@Autowired
	private ConfigDataSyncDao configDataSyncDao;

	@GetMapping("/activity")
	public ResponseEntity<?> activity() throws ParseException, JSONException, IOException {
		HashMap<String, Object> object = new HashMap<>();

		object.put("status", HttpStatus.OK);
		object.put("message", "Appointment is alive");
		object.put("responseTime", new Date());
		object.put("configDataSyncStatus", configDataSyncDao.getConfigDataSync(ConfigFacilityDataType.APPOINTMENT));

		return new ResponseEntity<>(object, HttpStatus.OK);
	}

}
