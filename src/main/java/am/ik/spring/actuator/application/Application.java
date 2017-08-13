package am.ik.spring.actuator.application;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Application implements Serializable {
	private final String applicationId;
	private final String applicationName;
	private final String url;
	private final Boolean readSensitiveData;

	@JsonCreator
	public Application(@JsonProperty("applicationId") String applicationId,
			@JsonProperty("applicationName") String applicationName,
			@JsonProperty("url") String url,
			@JsonProperty("readSensitiveData") Boolean readSensitiveData) {
		this.applicationId = Objects.toString(applicationId,
				UUID.randomUUID().toString());
		this.applicationName = applicationName;
		this.url = url;
		this.readSensitiveData = readSensitiveData == null ? true : readSensitiveData;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getUrl() {
		return url;
	}

	public boolean isReadSensitiveData() {
		return readSensitiveData;
	}

}
