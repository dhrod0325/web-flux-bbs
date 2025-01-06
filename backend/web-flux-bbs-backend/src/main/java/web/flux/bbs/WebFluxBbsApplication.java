package web.flux.bbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableR2dbcAuditing
@EnableWebFlux
@SpringBootApplication
public class WebFluxBbsApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebFluxBbsApplication.class, args);
    }
}
