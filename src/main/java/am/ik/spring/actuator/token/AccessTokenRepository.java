package am.ik.spring.actuator.token;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class AccessTokenRepository {
	private final AccessTokenMapper accessTokenMapper;

	public AccessTokenRepository(AccessTokenMapper accessTokenMapper) {
		this.accessTokenMapper = accessTokenMapper;
	}

	public Mono<AccessToken> findById(String applicationId) {
		return Mono.defer(
				() -> Mono.justOrEmpty(this.accessTokenMapper.findById(applicationId)))
				.subscribeOn(Schedulers.elastic());
	}

	public Mono<AccessToken> save(Mono<AccessToken> accessTokenMono) {
		return accessTokenMono //
				.publishOn(Schedulers.elastic()) //
				.map(this.accessTokenMapper::save) //
				.then(accessTokenMono);
	}

	public Mono<Void> delete(String applicationId) {
		return Mono.fromRunnable(() -> this.accessTokenMapper.delete(applicationId))
				.subscribeOn(Schedulers.elastic()) //
				.then();
	}

}
