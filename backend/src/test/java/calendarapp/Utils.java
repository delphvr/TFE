package calendarapp;

import org.springframework.test.web.reactive.server.WebTestClient;

import calendarapp.model.Project;
import calendarapp.model.Rehearsal;
import calendarapp.model.User;
import calendarapp.model.Vacation;

public class Utils {

    static public User pushUser(String jsonData, WebTestClient webTestClient){
        User user = webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(jsonData)
            .exchange()
            .expectBody(User.class)
            .returnResult()
            .getResponseBody();
        return user;
    }

    static public Project pushProject(String jsonData, WebTestClient webTestClient){
        Project project =  webTestClient.post().uri("/api/projects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(jsonData)
            .exchange()
            .expectBody(Project.class)
            .returnResult()
            .getResponseBody();
        return project;
    }

    static public Rehearsal pushRehearsal(String jsonData, WebTestClient webTestClient){
        Rehearsal rehearsal = webTestClient.post().uri("/api/rehearsals")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(jsonData)
            .exchange()
            .expectBody(Rehearsal.class)
            .returnResult()
            .getResponseBody();
        return rehearsal;
    }

    static public Vacation pushVacation(String jsonData, WebTestClient webTestClient){
        Vacation vacation = webTestClient.post().uri("/api/vacations")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(jsonData)
            .exchange()
            .expectBody(Vacation.class)
            .returnResult()
            .getResponseBody();
        return vacation;
    }
    
}
