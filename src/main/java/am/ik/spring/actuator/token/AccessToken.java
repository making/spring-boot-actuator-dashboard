package am.ik.spring.actuator.token;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessToken implements Serializable {
	private final String applicationId;
	private final String token;

	@JsonCreator
	public AccessToken(@JsonProperty("applicationId") String applicationId,
			@JsonProperty("issueToken") String token) {
		this.applicationId = applicationId;
		this.token = token;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public String getToken() {
		return token;
	}
}
