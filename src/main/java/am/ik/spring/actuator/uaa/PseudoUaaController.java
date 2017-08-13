package am.ik.spring.actuator.uaa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class PseudoUaaController {
	private final JwtTokenConverter jwtTokenConverter;

	public PseudoUaaController(JwtTokenConverter jwtTokenConverter) {
		this.jwtTokenConverter = jwtTokenConverter;
	}

	@GetMapping("token_keys")
	public Mono<Object> tokenKeys() {
		Map<String, Object> json = new HashMap<>();
		json.put("keys", Collections.singletonList(this.jwtTokenConverter.getKey()));
		return Mono.just(json);
	}

}
