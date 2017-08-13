package am.ik.spring.actuator.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import am.ik.spring.actuator.SpringBootActuatorDashboardProps;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/applications")
public class ApplicationController {
	private final ApplicationRepository applicationRepository;
	private final SpringBootActuatorDashboardProps props;
	private final Mono<Application> notFound = Mono
			.defer(() -> Mono.error(new IllegalArgumentException("Not Found")));

	public ApplicationController(ApplicationRepository applicationRepository,
			SpringBootActuatorDashboardProps props) {
		this.applicationRepository = applicationRepository;
		this.props = props;
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

	@GetMapping("{applicationId}/properties")
	public Mono<String> properties(@PathVariable String applicationId) {
		return Flux
				.just("VCAP_APPLICATION={}", "VCAP_SERVICES={}",
						"vcap.application.application_id=" + applicationId,
						"vcap.application.cf_api=" + props.getExternalUrl(),
						"# management.cloudfoundry.skip-ssl-validation=true # if needed")
				.collect(Collectors.joining(System.lineSeparator()));
	}
}
