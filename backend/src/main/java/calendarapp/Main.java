package calendarapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//http://localhost:8080/swagger-ui.html

@SpringBootApplication
@EnableJpaRepositories(basePackages = "calendarapp.repository")
@EntityScan(basePackages = "calendarapp.model")
@PropertySource("classpath:data.properties")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}