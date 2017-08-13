package am.ik.spring.actuator.application;

import java.util.List;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class ApplicationRepository {
	private final ApplicationMapper applicationMapper;

	public ApplicationRepository(ApplicationMapper applicationMapper) {
		this.applicationMapper = applicationMapper;
	}

	public Mono<List<Application>> findAll() {
		return Mono.fromCallable(this.applicationMapper::findAll) //
				.subscribeOn(Schedulers.elastic());
	}

	public Mono<Application> findById(String id) {
		return Mono.defer(() -> Mono.justOrEmpty(this.applicationMapper.findById(id)))
				.subscribeOn(Schedulers.elastic());
	}

	public Mono<Application> save(Mono<Application> applicationMono) {
		return applicationMono //
				.publishOn(Schedulers.elastic()) //
				.map(this.applicationMapper::save);
	}

	public Mono<Void> delete(String id) {
		return Mono.fromRunnable(() -> this.applicationMapper.delete(id))
				.subscribeOn(Schedulers.elastic()) //
				.then();
	}
}
