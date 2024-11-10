package calendarapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


//curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"firstName\":\"Del\", \"lastName\":\"Vr\", \"email\":\"d.vr@mail.com\"}"


@SpringBootApplication
@EnableJpaRepositories(basePackages = "calendarapp.repository")
@EntityScan(basePackages = "calendarapp.model")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }
}