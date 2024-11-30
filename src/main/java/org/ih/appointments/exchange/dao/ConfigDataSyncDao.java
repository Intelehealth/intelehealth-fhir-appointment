package org.ih.appointments.exchange.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.ih.appointments.exchange.datatype.ConfigFacilityDataType;
import org.ih.appointments.exchange.dto.ConfigDataSync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConfigDataSyncDao {

	@Autowired
	private NamedParameterJdbcTemplate template;

	public ConfigDataSync getConfigDataSync(ConfigFacilityDataType type) {
		String sql = "select c.id id, c.name name, c.status status from config_data_sync_module c where id=:id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", type.getValue());
		try {
			ConfigDataSync dto = template.queryForObject(sql, params, new RowMapper<ConfigDataSync>() {
				@Override
				public ConfigDataSync mapRow(ResultSet rs, int rowNum) throws SQLException {
					ConfigDataSync dto = new ConfigDataSync();
					dto.setId(rs.getInt("id"));
					dto.setName(rs.getString("name"));
					dto.setStatus(rs.getBoolean("status"));
					return dto;
				}
			});
			return dto;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}

	}

}
