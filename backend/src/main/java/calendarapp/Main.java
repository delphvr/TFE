package calendarapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


//curl -X POST "http://localhost:8080/api/users" -H "Content-Type: application/json" -d "{\"firstName\": \"Del\", \"lastName\": \"vr\", \"email\": \"del.vr@mail.com\", \"professions\": [\"Danseur\"], \"isOrganizer\": true}" 
//curl -X POST "http://localhost:8080/api/professions" -H "Content-Type: application/json" -d "{\"profession\": \"Danseur\"}"


@SpringBootApplication
@EnableJpaRepositories(basePackages = "calendarapp.repository")
@EntityScan(basePackages = "calendarapp.model")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }
}