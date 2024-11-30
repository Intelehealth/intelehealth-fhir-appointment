package org.ih.appointments.exchange.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.ih.appointments.exchange.dto.RequestAppointmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExternalAppointmentDao {
	
	@Autowired
	private NamedParameterJdbcTemplate template;

	public List<RequestAppointmentDTO> getExternalAppointmentByDate(String date) {
		String sql = "SELECT  "
				+ " ea.request_id request_id,"
				+ " ea.facility_uuid location,"
				+ " ea.facility_name location_name,"
				+ " ea.requester_id practitioner, "
				+ " ea.patient_uuid patient_id,"
				+ " ea.slot slot,"
				+ " ea.duration duration,"
				+ " ea.service_category service_category,"
				+ " ea.service_type service_type,"
				+ " ea.specialty specialty"
				+ " from external_appointment ea"
				+ " where ea.status='booked' and (ea.date_changed >= :date or ea.date_created >=:date)";

		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("date", date);

		try {
			
			List<RequestAppointmentDTO> items = template.query(sql, params, new RowMapper<RequestAppointmentDTO>() {
				@Override
				public RequestAppointmentDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					RequestAppointmentDTO dto = new RequestAppointmentDTO();
					dto.setRequestId(rs.getString("request_id"));
					dto.setLocation(rs.getString("location"));
					dto.setLocationName(rs.getString("location_name"));
					dto.setPractitioner(rs.getString("practitioner"));
					dto.setPatientId(rs.getString("patient_id"));
					dto.setSlot(rs.getString("slot"));
					dto.setDuration(rs.getInt("duration"));
					dto.setServiceCategory(rs.getString("service_category"));
					dto.setServiceType(rs.getString("service_type"));
					dto.setSpecialty(rs.getString("specialty"));
					return dto;
				}
			});
			
			return items;
			
		}catch(EmptyResultDataAccessException e) {
			e.printStackTrace();
			return new ArrayList<RequestAppointmentDTO>();
		}

	}
	
	public RequestAppointmentDTO getExternalAppointmentByDateAndPatientId(String date, String patientId) {
		String sql = "SELECT  "
				+ " ea.request_id request_id,"
				+ " ea.facility_uuid location,"
				+ " ea.facility_name location_name,"
				+ " ea.requester_id practitioner, "
				+ " ea.patient_uuid patient_id,"
				+ " ea.slot slot,"
				+ " ea.duration duration,"
				+ " ea.service_category service_category,"
				+ " ea.service_type service_type,"
				+ " ea.specialty specialty"
				+ " from external_appointment ea"
				+ " where  ea.status='booked' and date(ea.date_created) = :date and ea.patient_uuid =:patientId"
				+ " order by ea.date_created desc limit 1";

		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("date", date);
		params.addValue("patientId", patientId);

		try {
			RequestAppointmentDTO requestAppointment = template.queryForObject(sql, params, new RowMapper<RequestAppointmentDTO>() {
				@Override
				public RequestAppointmentDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					RequestAppointmentDTO dto = new RequestAppointmentDTO();
					dto.setRequestId(rs.getString("request_id"));
					dto.setLocation(rs.getString("location"));
					dto.setLocationName(rs.getString("location_name"));
					dto.setPractitioner(rs.getString("practitioner"));
					dto.setPatientId(rs.getString("patient_id"));
					dto.setSlot(rs.getString("slot"));
					dto.setDuration(rs.getInt("duration"));
					dto.setServiceCategory(rs.getString("service_category"));
					dto.setServiceType(rs.getString("service_type"));
					dto.setSpecialty(rs.getString("specialty"));
					return dto;
				}
			});

			return requestAppointment;
		}catch(EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

}
