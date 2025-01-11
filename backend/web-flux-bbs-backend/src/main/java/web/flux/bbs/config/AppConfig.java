package web.flux.bbs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableR2dbcAuditing
@EnableWebFlux
@Configuration
public class AppConfig {
}
