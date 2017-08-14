package am.ik.spring.actuator.capi;

import static java.util.Collections.singletonMap;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import am.ik.spring.actuator.SpringBootActuatorDashboardProps;
import am.ik.spring.actuator.application.ApplicationRepository;
import am.ik.spring.actuator.token.AccessTokenService;
import reactor.core.publisher.Mono;

@RestController
public class PseudoCloudController {
	private final ApplicationRepository applicationRepository;
	private final AccessTokenService accessTokenService;
	private final SpringBootActuatorDashboardProps props;

	public PseudoCloudController(ApplicationRepository applicationRepository,
			AccessTokenService accessTokenService,
			SpringBootActuatorDashboardProps props) {
		this.applicationRepository = applicationRepository;
		this.accessTokenService = accessTokenService;
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
		return this.accessTokenService.checkToken(applicationId, token) //
				.flatMap(accessToken -> this.applicationRepository.findById(applicationId)
						.map(application -> ResponseEntity
								.ok(singletonMap("read_sensitive_data",
										application.isReadSensitiveData()))) //
						.switchIfEmpty(forbidden()))
				.switchIfEmpty(forbidden());
	}

	Mono<ResponseEntity<Map<String, Boolean>>> forbidden() {
		return Mono.fromCallable(() -> ResponseEntity.notFound().build());
	}
}
