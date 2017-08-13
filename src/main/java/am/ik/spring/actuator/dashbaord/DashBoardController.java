package am.ik.spring.actuator.dashbaord;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashBoardController {
	@GetMapping("/")
	public ResponseEntity index() {
		return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header("Location", "/dashboard").build();
	}

	@GetMapping("/dashboard/**")
	public Resource dashboard() {
		return new ClassPathResource("static/index.html");
	}
}
