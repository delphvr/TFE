package org.example;

//import java.util.ArrayList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//curl -X POST http://localhost:8080/addUser -H "Content-Type: application/json" -d "{\"firstName\":\"test\",\"lastName\":\"t\",\"email\":\"test.t@t.com\",\"isOrganizer\":true,\"professions\":[\"dancer\"]}"

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

        //Database db = new Database();
        //db.getConnection();

        // Just once at the beginning for table initialization
        // db.dropAllTables();
        // db.createInitialTables();

        //ArrayList<String> professions = new ArrayList<>();
        //professions.add("dancer");
        // db.addToProfessionValuesTable(professions);
        // db.addUser("ftest", "ltest", "tes@test.com", true, professions);

    }
}