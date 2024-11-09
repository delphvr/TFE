package calendarapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


//curl -X POST http://localhost:8080/addUser -H "Content-Type: application/json" -d "{\"firstName\":\"test\",\"lastName\":\"t\",\"email\":\"test.t@t.com\",\"isOrganizer\":true,\"professions\":[\"dancer\"]}"

@SpringBootApplication
@EnableJpaRepositories(basePackages = "calendarapp.repository")
@EntityScan(basePackages = "calendarapp.entity")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }
}