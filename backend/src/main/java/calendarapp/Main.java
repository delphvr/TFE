package calendarapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


//curl -X POST "http://localhost:8080/api/users" -H "Content-Type: application/json" -d "{\"firstName\": \"Del\", \"lastName\": \"vr\", \"email\": \"del.vr@mail.com\", \"professions\": [\"Danseur\"], \"isOrganizer\": true}" 
//curl -X POST "http://localhost:8080/api/professions" -H "Content-Type: application/json" -d "{\"profession\": \"Danseur\"}"
//curl -X POST "http://localhost:8080/api/projects" -H "Content-Type: application/json" -d "{\"name\": \"Christmas show\", \"description\": \"Winter show with santa...\", \"beginningDate\": \"2020-07-01\", \"endingDate\": \"2020-12-26\", \"organizerEmail\": \"del.vr@mail.com\"}"


@SpringBootApplication
@EnableJpaRepositories(basePackages = "calendarapp.repository")
@EntityScan(basePackages = "calendarapp.model")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }
}