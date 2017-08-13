package am.ik.spring.actuator.capi;

import static java.util.Collections.singletonMap;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import am.ik.spring.actuator.SpringBootActuatorDashboardProps;
import am.ik.spring.actuator.application.ApplicationRepository;
import am.ik.spring.actuator.token.AccessTokenRepository;
import reactor.core.publisher.Mono;

@RestController
public class PseudoCloudController {
	private final ApplicationRepository applicationRepository;
	private final AccessTokenRepository accessTokenRepository;
	private final SpringBootActuatorDashboardProps props;

	public PseudoCloudController(ApplicationRepository applicationRepository,
			AccessTokenRepository accessTokenRepository,
			SpringBootActuatorDashboardProps props) {
		this.applicationRepository = applicationRepository;
		this.accessTokenRepository = accessTokenRepository;
		this.props = props;
	}

	@GetMapping("info")
	public Mono<Object> info() {
		return Mono.just(singletonMap("token_endpoint", props.getExternalUrl()));
	}

	@GetMapping("/v2/apps/{applicationId}/permissions")
	public Mono<ResponseEntity<Map<String, Boolean>>> permissions(
			@PathVariable String applicationId,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
		String token = authorization.substring(7);
		return this.accessTokenRepository.findById(applicationId) //
				.filter(accessToken -> Objects.equals(accessToken.getToken(), token))
				.flatMap(accessToken -> this.applicationRepository.findById(applicationId)
						.map(application -> ResponseEntity
								.ok(singletonMap("read_sensitive_data",
										application.isReadSensitiveData()))) //
						.switchIfEmpty(Mono
								.fromCallable(() -> ResponseEntity.notFound().build())))
				.switchIfEmpty(Mono.fromCallable(
						() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build()));
	}
}
