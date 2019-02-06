package am.ik.spring.actuator.proxy;

import static org.springframework.http.HttpHeaders.*;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import am.ik.spring.actuator.application.Application;
import am.ik.spring.actuator.application.ApplicationRepository;
import am.ik.spring.actuator.token.AccessToken;
import am.ik.spring.actuator.token.AccessTokenService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("proxy/{applicationId}/**")
public class ProxyController {
	private final ApplicationRepository applicationRepository;
	private final AccessTokenService accessTokenService;
	private final WebClient webClient;

	public ProxyController(ApplicationRepository applicationRepository,
			AccessTokenService accessTokenService, WebClient.Builder builder) {
		this.applicationRepository = applicationRepository;
		this.accessTokenService = accessTokenService;
		this.webClient = builder.build();
	}

	@GetMapping
	public Mono<ResponseEntity<String>> get(@PathVariable String applicationId,
			ServerHttpRequest request) {
		return configure(this.webClient.get(), applicationId, request) //
				.flatMap(spec -> spec.exchange() //
						.flatMap(res -> res.bodyToMono(String.class) //
								.map(b -> ResponseEntity.status(res.statusCode()).body(b)) //
								.switchIfEmpty(emptyResponse(res))));
	}

	@PostMapping
	public Mono<ResponseEntity<String>> post(@PathVariable String applicationId,
			ServerHttpRequest request) {
		return configure(this.webClient.post(), applicationId, request) //
				.flatMap(spec -> spec
						.header(CONTENT_TYPE, request.getHeaders().getFirst(CONTENT_TYPE)) //
						.body(request.getBody(), DataBuffer.class) //
						.exchange() //
						.flatMap(res -> res.bodyToMono(String.class) //
								.map(b -> ResponseEntity.status(res.statusCode()).body(b)) //
								.switchIfEmpty(emptyResponse(res))));
	}

	Mono<ResponseEntity<String>> emptyResponse(ClientResponse res) {
		return Mono.fromCallable(() -> ResponseEntity.status(res.statusCode()).build());
	}

	<S extends WebClient.RequestHeadersSpec<S>> Mono<S> configure(
			WebClient.RequestHeadersUriSpec<S> spec, String applicationId,
			ServerHttpRequest request) {
		Mono<Application> applicationMono = this.applicationRepository
				.findById(applicationId);
		Mono<AccessToken> accessTokenMono = this.accessTokenService
				.issueToken(applicationId);

		// / proxy / {applicationId} / **
		// ^___^___^________^________^__^
		// 0___1___2________3________4__5
		PathContainer wildcard = request.getPath().subPath(5);

		return Mono.zip(applicationMono, accessTokenMono) //
				.map(tpl -> spec
						.uri(tpl.getT1().getUrl() + "/cloudfoundryapplication/"
								+ wildcard.value())
						.header(AUTHORIZATION, "bearer " + tpl.getT2().getToken()) //
						.header(REFERER, request.getHeaders().getFirst(REFERER)));
	}
}
