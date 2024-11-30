package org.ih.appointments.exchange.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommonOperationDao {

	@Autowired
	private NamedParameterJdbcTemplate template;

	public String findAppointmentServerUrlByLocationV2(String locationUuid) {
		String sql = "SELECT appointment_api FROM config_fcility WHERE facility_uuid=:uuid";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("uuid", locationUuid);
		try {
			String apiURL = template.queryForObject(sql, params, String.class);
			return apiURL;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}

	}
}
