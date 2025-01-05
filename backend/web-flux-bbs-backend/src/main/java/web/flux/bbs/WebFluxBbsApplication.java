package web.flux.bbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = "web.flux.bbs.domain") //
@SpringBootApplication
public class WebFluxBbsApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebFluxBbsApplication.class, args);
    }
}
