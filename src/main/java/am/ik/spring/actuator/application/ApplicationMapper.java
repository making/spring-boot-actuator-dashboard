package am.ik.spring.actuator.application;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ApplicationMapper {
	private final JdbcTemplate jdbcTemplate;
	private final RowMapper<Application> rowMapper = (rs, i) -> new Application(
			rs.getString("application_id"), rs.getString("application_name"),
			rs.getString("url"), rs.getBoolean("read_sensitive_data"));

	public ApplicationMapper(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Application> findAll() {
		return this.jdbcTemplate.query(
				"SELECT application_id, application_name, url, read_sensitive_data FROM application",
				rowMapper);
	}

	public Optional<Application> findById(String id) {
		try {
			return Optional.of(this.jdbcTemplate.queryForObject(
					"SELECT application_id, application_name, url, read_sensitive_data FROM application WHERE application_id = ?",
					rowMapper, id));
		}
		catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	@Transactional
	public Application save(Application application) {
		this.jdbcTemplate.update(
				"INSERT INTO application(application_id, application_name, url, read_sensitive_data) VALUES (?, ?, ?, ?)",
				application.getApplicationId(), application.getApplicationName(),
				application.getUrl(), application.isReadSensitiveData());
		return application;
	}

	@Transactional
	public void delete(String id) {
		this.jdbcTemplate.update("DELETE FROM application WHERE application_id = ?", id);
	}
}
