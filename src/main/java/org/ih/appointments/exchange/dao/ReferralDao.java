package org.ih.appointments.exchange.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	
	public List<ReferralDTO> getReferralByDate(String date) {
		String sql="SELECT"
				+ "    e.uuid AS encounter_id,"
				+ "    p.uuid AS patient_id,"
				+ "    p2.uuid AS practitioner_id,"
				+ "    o.value_text AS reason,"
				+ "    CONCAT(pn.given_name, ' ', pn.family_name) AS patient_name,"
				+ "    CONCAT(pn2.given_name, ' ', pn2.family_name) AS practitioner_name, "
				+ "    date(o.date_created) created "
				+ "FROM"
				+ "    obs o "
				+ "JOIN encounter e "
				+ "    ON o.encounter_id = e.encounter_id "
				+ "JOIN person p "
				+ "    ON o.person_id = p.person_id "
				+ "JOIN person_name pn "
				+ "    ON p.person_id = pn.person_id "
				+ "    AND pn.preferred = 1 "
				+ "JOIN users u "
				+ "    ON u.user_id = o.creator "
				+ "JOIN provider p2 "
				+ "    ON p2.person_id = u.person_id "
				+ "JOIN person_name pn2 "
				+ "    ON pn2.person_id = u.person_id "
				+ "    AND pn2.preferred = 1 "
				+ "WHERE"
				+ "    o.concept_id = 165238"
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
