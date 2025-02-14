package calendarapp.controller;

import calendarapp.model.Project;
import calendarapp.model.User;
import calendarapp.repository.ProjectRepository;
import calendarapp.repository.UserProjectRepository;
import calendarapp.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test") 
public class UserProjectControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserProjectRepository userProjectRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void cleanUpDatabase() {
        userProjectRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    /*
     * Tests add a user to an existing project
     */

    @Test
    public void testAddUser() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange();

        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Directrice'], 'isOrganizer': false}"
            .replace('\'', '"');

        User user = webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(user2Json)
            .exchange()
            .expectBody(User.class)
            .returnResult()
            .getResponseBody();

        String projectJson = "{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '2020-12-26', 'organizerEmail': 'del.vr@mail.com'}".replace('\'', '"');

        Project project =  webTestClient.post().uri("/api/projects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(projectJson)
            .exchange()
            .expectBody(Project.class)
            .returnResult()
            .getResponseBody();

        String userProjectJson = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur', 'Organizer']}"
                        .replace('\'', '"');

        webTestClient.post().uri("/api/userProjects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userProjectJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.projectId").isEqualTo(project.getId())
            .jsonPath("$.userId").isEqualTo(user.getId())
            .jsonPath("$.role").isArray()
            .jsonPath("$.role[0]").isEqualTo("Danseur")
            .jsonPath("$.role[1]").isEqualTo("Organizer");
    }

    @Test
    public void testAddUserUserNotExist() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange();

        String projectJson = "{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '2020-12-26', 'organizerEmail': 'del.vr@mail.com'}".replace('\'', '"');

        Project project =  webTestClient.post().uri("/api/projects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(projectJson)
            .exchange()
            .expectBody(Project.class)
            .returnResult()
            .getResponseBody();

        String userProjectJson = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur', 'Organizer']}"
                        .replace('\'', '"');

        webTestClient.post().uri("/api/userProjects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userProjectJson)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void testAddUserProjectNotExist() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange();

        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Directrice'], 'isOrganizer': false}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(user2Json)
            .exchange();

        String userProjectJson = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': 1, 'roles':  ['Danseur', 'Organizer']}"
                        .replace('\'', '"');

        webTestClient.post().uri("/api/userProjects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userProjectJson)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void testAddUserWrongMail() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange();

        String projectJson = "{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '2020-12-26', 'organizerEmail': 'del.vr@mail.com'}".replace('\'', '"');

        Project project =  webTestClient.post().uri("/api/projects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(projectJson)
            .exchange()
            .expectBody(Project.class)
            .returnResult()
            .getResponseBody();

        String userProjectJson = "{ 'userEmail': 'eve.mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur', 'Organizer']}"
                        .replace('\'', '"');

        webTestClient.post().uri("/api/userProjects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userProjectJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

}
