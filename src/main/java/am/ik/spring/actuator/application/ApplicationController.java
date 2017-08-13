package am.ik.spring.actuator.application;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/applications")
public class ApplicationController {
	private final ApplicationRepository applicationRepository;
	private final Mono<Application> notFound = Mono
			.defer(() -> Mono.error(new IllegalArgumentException("Not Found")));

	public ApplicationController(ApplicationRepository applicationRepository) {
		this.applicationRepository = applicationRepository;
	}

	@GetMapping
	public Mono<List<Application>> gets() {
		return this.applicationRepository.findAll();
	}

	@GetMapping("{applicationId}")
	public Mono<Application> get(@PathVariable String applicationId) {
		return this.applicationRepository.findById(applicationId) //
				.switchIfEmpty(notFound);
	}

	@PostMapping
	public Mono<Application> post(@RequestBody Mono<Application> application) {
		return this.applicationRepository.save(application);
	}

	@DeleteMapping("{applicationId}")
	public Mono<Void> delete(@PathVariable String applicationId) {
		return this.applicationRepository.delete(applicationId);
	}
}
