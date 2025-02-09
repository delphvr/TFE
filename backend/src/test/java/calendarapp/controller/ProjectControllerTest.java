package calendarapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import calendarapp.repository.ProjectRepository;
import calendarapp.repository.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test") 
public class ProjectControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void cleanUpDatabase() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    /*
     * Tests creation project
     */

    @Test
    public void testCreateProject() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange();

        String projectJson  = "{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '2020-12-26', 'organizerEmail': 'del.vr@mail.com'}".replace('\'', '"');

        webTestClient.post().uri("/api/projects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(projectJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.name").isEqualTo("Christmas show")
            .jsonPath("$.description").isEqualTo("Winter show with santa...")
            .jsonPath("$.beginningDate").isEqualTo("2020-07-01")
            .jsonPath("$.endingDate").isEqualTo("2020-12-26");
    }

    @Test
    public void testCreateProjectEmailNotFound() {
        String projectJson  = "{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '2020-12-26', 'organizerEmail': 'del.vr@mail.com'}".replace('\'', '"');

        webTestClient.post().uri("/api/projects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(projectJson)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void testCreateProjectWrongDateOrder() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange();

        String projectJson  = "{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '2020-06-29', 'organizerEmail': 'del.vr@mail.com'}".replace('\'', '"');

        webTestClient.post().uri("/api/projects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(projectJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    /*
     * Tests get projects of a user
     */

     @Test
     public void testGetUserProjects() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
 
        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange();
 
        String projectJson  = "{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '2020-12-26', 'organizerEmail': 'del.vr@mail.com'}".replace('\'', '"');
 
        webTestClient.post().uri("/api/projects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(projectJson)
            .exchange();
        
        webTestClient.get().uri("/api/projects/user/del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].name").isEqualTo("Christmas show")
            .jsonPath("$[0].description").isEqualTo("Winter show with santa...")
            .jsonPath("$[0].beginningDate").isEqualTo("2020-07-01")
            .jsonPath("$[0].endingDate").isEqualTo("2020-12-26");
     }

     @Test
     public void testGetUserProjectsEmpty() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
 
        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange();
        
        webTestClient.get().uri("/api/projects/user/del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .json("[]");
     }

     @Test
     public void testGetUserProjectsNotUser() {
        webTestClient.get().uri("/api/projects/user/del.vr@mail.com")
            .exchange()
            .expectStatus().isNotFound();
     }
    
}
