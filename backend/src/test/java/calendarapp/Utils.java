package calendarapp;

import java.util.List;
import java.util.Map;

import org.springframework.test.web.reactive.server.WebTestClient;

import calendarapp.model.CpResult;
import calendarapp.model.Project;
import calendarapp.model.Rehearsal;
import calendarapp.model.User;
import calendarapp.model.Vacation;
import calendarapp.model.WeeklyAvailability;
import calendarapp.response.UserProjectResponse;

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

    static public UserProjectResponse pushParticipantToProject(String userProjectJson, WebTestClient webTestClient){
        UserProjectResponse userProjectResponse = webTestClient.post().uri("/api/userProjects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userProjectJson)
            .exchange()
            .expectBody(UserProjectResponse.class)
            .returnResult()
            .getResponseBody();
        return userProjectResponse;
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

    static public List<WeeklyAvailability> pushAvailability(String jsonData, WebTestClient webTestClient){
        List<WeeklyAvailability> availability = webTestClient.post().uri("/api/availabilities")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(jsonData)
            .exchange()
            .expectBodyList(WeeklyAvailability.class)
            .returnResult()
            .getResponseBody();
        return availability;
    }

    static public List<CpResult> getCpResults(int projectId, WebTestClient webTestClient, boolean recompute){
        List<CpResult> results = webTestClient.get().uri("/api/projects/" + projectId + "/calendarCP?recompute=" + recompute)
           .exchange()
           .expectBodyList(CpResult.class)
           .returnResult()
           .getResponseBody();
        return results;
    }

    static public Map<Long, Map<Long, Boolean>> getCpPresencesResults(int projectId, WebTestClient webTestClient){
        Map<Long, Map<Long, Boolean>> presences = webTestClient.get().uri("/api/projects/" + projectId + "/CPpresences")
           .exchange()
           .expectBody(new org.springframework.core.ParameterizedTypeReference<Map<Long, Map<Long, Boolean>>>() {})
           .returnResult()
           .getResponseBody();
        return presences;
    }
    
}
