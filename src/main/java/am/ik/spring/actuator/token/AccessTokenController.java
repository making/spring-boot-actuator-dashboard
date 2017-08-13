package am.ik.spring.actuator.token;

import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/applications/{applicationId}/token")
public class AccessTokenController {
	private final AccessTokenService accessTokenService;
	private final AccessTokenRepository accessTokenRepository;
	private final Mono<AccessToken> notFound = Mono
			.defer(() -> Mono.error(new IllegalArgumentException("Not Found")));

	public AccessTokenController(AccessTokenService accessTokenService,
			AccessTokenRepository accessTokenRepository) {
		this.accessTokenService = accessTokenService;
		this.accessTokenRepository = accessTokenRepository;
	}

	@PostMapping
	public Mono<AccessToken> token(@PathVariable String applicationId) {
		return this.accessTokenService.issueToken(applicationId) //
				.switchIfEmpty(notFound);
	}

	@DeleteMapping
	public Mono<Void> revoke(@PathVariable String applicationId) {
		return this.accessTokenRepository.delete(applicationId);
	}
}
