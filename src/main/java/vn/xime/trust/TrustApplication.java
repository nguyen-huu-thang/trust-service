package vn.xime.trust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TrustApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrustApplication.class, args);
    }
}
