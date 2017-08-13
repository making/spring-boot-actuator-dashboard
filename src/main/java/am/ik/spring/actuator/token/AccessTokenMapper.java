package am.ik.spring.actuator.token;

import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AccessTokenMapper {
	private final JdbcTemplate jdbcTemplate;
	private final RowMapper<AccessToken> rowMapper = (rs,
			i) -> new AccessToken(rs.getString("application_id"), rs.getString("token"));

	public AccessTokenMapper(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Optional<AccessToken> findById(String applicationId) {
		try {
			return Optional.of(this.jdbcTemplate.queryForObject(
					"SELECT application_id, token FROM access_token WHERE application_id = ?",
					rowMapper, applicationId));
		}
		catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	@Transactional
	public AccessToken save(AccessToken accessToken) {
		this.jdbcTemplate.update(
				"INSERT INTO access_token(application_id, token) VALUES (?, ?)",
				accessToken.getApplicationId(), accessToken.getToken());
		return accessToken;
	}

	@Transactional
	public void delete(String applicationId) {
		this.jdbcTemplate.update("DELETE FROM access_token WHERE application_id = ?",
				applicationId);
	}

}
