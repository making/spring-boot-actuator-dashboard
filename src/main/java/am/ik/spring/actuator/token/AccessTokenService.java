package am.ik.spring.actuator.token;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.nimbusds.jwt.JWTClaimsSet;

import am.ik.spring.actuator.SpringBootActuatorDashboardProps;
import am.ik.spring.actuator.application.ApplicationRepository;
import am.ik.spring.actuator.uaa.JwtTokenConverter;
import reactor.core.publisher.Mono;

@Service
public class AccessTokenService {
	private final ApplicationRepository applicationRepository;
	private final AccessTokenRepository accessTokenRepository;
	private final JwtTokenConverter jwtTokenConverter;
	private final SpringBootActuatorDashboardProps props;

	public AccessTokenService(ApplicationRepository applicationRepository,
			AccessTokenRepository accessTokenRepository,
			JwtTokenConverter jwtTokenConverter, SpringBootActuatorDashboardProps props) {
		this.applicationRepository = applicationRepository;
		this.accessTokenRepository = accessTokenRepository;
		this.jwtTokenConverter = jwtTokenConverter;
		this.props = props;
	}

	public Mono<AccessToken> issueToken(String applicationId) {
		return this.applicationRepository.findById(applicationId) //
				.switchIfEmpty(Mono.empty()) //
				.flatMap(
						application -> this.accessTokenRepository.findById(applicationId)) //
				.switchIfEmpty(Mono
						.fromCallable(
								() -> new AccessToken(applicationId, generateToken())) //
						.compose(this.accessTokenRepository::save));
	}

	public Mono<AccessToken> checkToken(String applicationId, String token) {
		return this.accessTokenRepository.findById(applicationId) //
				.filter(accessToken -> Objects.equals(accessToken.getToken(), token));
	}

	String generateToken() {
		Instant now = Instant.now();
		Instant exp = now.plus(1, ChronoUnit.DAYS);

		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
				.issuer(props.getExternalUrl() + "/oauth/token") //
				.expirationTime(Date.from(exp)) //
				.issueTime(Date.from(now)) //
				.claim("scope", Collections.singletonList("actuator.read")) //
				.build();

		return this.jwtTokenConverter.sign(claimsSet).serialize();
	}
}
