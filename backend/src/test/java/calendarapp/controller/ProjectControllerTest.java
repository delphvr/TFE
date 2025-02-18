package calendarapp.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import calendarapp.Utils;
import calendarapp.model.Project;
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
        Utils.pushUser(userJson, webTestClient);

        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"'); 

        webTestClient.post().uri("/api/projects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(projectJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.name").isEqualTo("Christmas show")
            .jsonPath("$.description").isEqualTo("Winter show with santa...")
            .jsonPath("$.beginningDate").isEqualTo("2020-07-01")
            .jsonPath("$.endingDate").isEqualTo(futureEndingDate);
    }

    @Test
    public void testCreateProjectEmailNotFound() {
        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"'); 

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
        Utils.pushUser(userJson, webTestClient);

        String projectJson = "{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '2020-06-29', 'organizerEmail': 'del.vr@mail.com'}".replace('\'', '"');

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
        Utils.pushUser(userJson, webTestClient);
 
        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"'); 
        Utils.pushProject(projectJson, webTestClient);
        
        webTestClient.get().uri("/api/projects/user/del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].name").isEqualTo("Christmas show")
            .jsonPath("$[0].description").isEqualTo("Winter show with santa...")
            .jsonPath("$[0].beginningDate").isEqualTo("2020-07-01")
            .jsonPath("$[0].endingDate").isEqualTo(futureEndingDate);
     }

     @Test
     public void testGetUserProjectsEmpty() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);
        
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

    /*
     * Tests update a project
     */

    @Test
    public void testUpdateProject() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);
 
        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"'); 
        Project project =  Utils.pushProject(projectJson, webTestClient);
        
        String projectUpdatedJson = ("{ 'name': 'Christmas show 2.0', 'description': 'Winter show with santa and its elfs', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"'); 
 
        webTestClient.put().uri("/api/projects/" + project.getId())
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(projectUpdatedJson)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.name").isEqualTo("Christmas show 2.0")
            .jsonPath("$.description").isEqualTo("Winter show with santa and its elfs")
            .jsonPath("$.beginningDate").isEqualTo("2020-07-01")
            .jsonPath("$.endingDate").isEqualTo(futureEndingDate);
     }

     @Test
    public void testUpdateProjectNotFound() {        
        String projectUpdatedJson = "{ 'name': 'Christmas show 2.0', 'description': 'Winter show with santa and its elfs', 'beginningDate': '2020-07-01', 'endingDate': '2020-12-26'}".replace('\'', '"');
 
        webTestClient.put().uri("/api/projects/1")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(projectUpdatedJson)
            .exchange()
            .expectStatus().isNotFound();
    }

    /*
     * Tests update a project
     */

    @Test
    public void testDeleteProject() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        webTestClient.delete().uri("/api/projects/" + project.getId())
            .exchange()
            .expectStatus().isNoContent();

        webTestClient.get().uri("/api/projects/user/del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .json("[]");
    }

    @Test
    public void testDeleteProjectNotFound() {
        webTestClient.delete().uri("/api/projects/1")
            .exchange()
            .expectStatus().isNotFound();
    }
     
}
