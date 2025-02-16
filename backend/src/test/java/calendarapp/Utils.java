package calendarapp;

import org.springframework.test.web.reactive.server.WebTestClient;

import calendarapp.model.Project;
import calendarapp.model.User;

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
    
}
