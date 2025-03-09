package org.ih.appointments.exchange.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ih.appointments.exchange.dto.ReferralDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReferralDao {
	
	@Autowired
	private NamedParameterJdbcTemplate template;  
	
	public List<ReferralDTO> getReferralByDateDeprecated(String date) {
		String sql = "SELECT "
	             + "    e.uuid AS encounter_id, "
	             + "    p.uuid AS patient_id, "
	             + "    p2.uuid AS practitioner_id, "
	             + "    pi2.identifier AS mpi_id, "
	             + "    o.value_text AS reason, "
	             + "    CONCAT(pn.given_name, ' ', pn.family_name) AS patient_name, "
	             + "    CONCAT(pn2.given_name, ' ', pn2.family_name) AS practitioner_name, "
	             + "    DATE(o.date_created) AS created "
	             + "FROM "
	             + "    obs o "
	             + "JOIN "
	             + "    encounter e ON o.encounter_id = e.encounter_id "
	             + "JOIN "
	             + "    person p ON o.person_id = p.person_id "
	             + "JOIN "
	             + "    person_name pn ON p.person_id = pn.person_id "
	             + "    AND pn.preferred = 1 "
	             + "JOIN "
	             + "    users u ON u.user_id = o.creator "
	             + "JOIN "
	             + "    provider p2 ON p2.person_id = u.person_id "
	             + "JOIN "
	             + "    person_name pn2 ON pn2.person_id = u.person_id "
	             + "    AND pn2.preferred = 1 "
	             + "JOIN "
	             + "    patient_identifier pi2 ON pi2.patient_id = p.person_id "
	             + "JOIN "
	             + "    patient_identifier_type pit ON pit.patient_identifier_type_id = pi2.identifier_type "
	             + "WHERE "
	             + "    o.concept_id = 165238 "
	             + "    AND pit.name = 'MPI'"
				 + "    AND ("
				 + "        o.date_created >= :dateValue"
				 + "        OR o.obs_datetime >= :dateValue"
				 + "    )";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		
		params.addValue("dateValue", date);
		
		try {
			List<ReferralDTO> items = template.query(sql, params, new RowMapper<ReferralDTO>() {
				@Override
				public ReferralDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					ReferralDTO dto = new ReferralDTO();
					dto.setEncounterId(rs.getString("encounter_id"));
					dto.setPatientId(rs.getString("patient_id"));
					dto.setMpiId(rs.getString("mpi_id"));
					dto.setPatientName(rs.getString("patient_name"));
					dto.setPractitioner(rs.getString("practitioner_id"));
					dto.setPractitionerName(rs.getString("practitioner_name"));
					dto.setReason(rs.getString("reason"));
					dto.setCreated(rs.getString("created"));
					return dto;
				}
			});
			
			Map<String, ReferralDTO> map = items.stream()
				    .collect(Collectors.toMap(ReferralDTO::getEncounterId, dto -> dto, (existing, replacement) -> existing));
			
			return new ArrayList<>(map.values());
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ReferralDTO>();
		}
	}
	
	public List<ReferralDTO> getReferralByDateV2(String date) {
		String sql = " SELECT "
				+ "	er.facility_name ,"
				+ "	er.facility_uuid ,"
				+ "	e.uuid AS encounter_id,"
				+ "	er.patient_id AS patient_id,"
				+ "	er.patient_id AS mpi_id,"
				+ "	er.reason AS reason,"
				+ "	er.patient_name AS patient_name,"
				+ "	er.referrer_id AS practitioner_id,"
				+ "	er.referred_by AS practitioner_name,"
				+ "	er.date_created  AS created,"
				+ "	v.uuid AS visitId,"
				+ "	e.encounter_type AS encounterType"
				+ " FROM"
				+ "	external_referral er"
				+ " INNER JOIN visit v ON"
				+ "	er.visit_id = v.uuid"
				+ " INNER JOIN encounter e ON"
				+ "	v.visit_id = e.visit_id"
				+ "	AND e.encounter_type = 14"
				+ " WHERE "
				+ "	er.date_changed >= :date"
				+ "	or er.date_created >=:date"
				+ "	or e.date_created >=:date"
				+ "	or e.date_changed >=:date"
				+ "	or v.date_changed >= :date"
				+ "	or v.date_created >= :date";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		
		params.addValue("date", date);
		
		try {
			List<ReferralDTO> items = template.query(sql, params, new RowMapper<ReferralDTO>() {
				@Override
				public ReferralDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					ReferralDTO dto = new ReferralDTO();
					dto.setFacilityName(rs.getString("facility_name"));
					dto.setFacilityId(rs.getString("facility_uuid"));
					dto.setEncounterId(rs.getString("encounter_id"));
					dto.setPatientId(rs.getString("patient_id"));
					dto.setMpiId(rs.getString("mpi_id"));
					dto.setPatientName(rs.getString("patient_name"));
					dto.setPractitioner(rs.getString("practitioner_id"));
					dto.setPractitionerName(rs.getString("practitioner_name"));
					dto.setReason(rs.getString("reason"));
					dto.setCreated(rs.getString("created"));
					return dto;
				}
			});
			
			return items;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ReferralDTO>();
		}
	}

}
