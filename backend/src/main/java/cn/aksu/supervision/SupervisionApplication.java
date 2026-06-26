package cn.aksu.supervision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SupervisionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupervisionApplication.class, args);
    }
}
