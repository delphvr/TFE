package calendarapp.controller;

import calendarapp.Utils;
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
import static org.hamcrest.Matchers.hasItems;

import java.time.LocalDate;

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
        Utils.pushUser(userJson, webTestClient);

        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Directrice'], 'isOrganizer': false}"
            .replace('\'', '"');
        User user = Utils.pushUser(user2Json, webTestClient);

        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

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
        Utils.pushUser(userJson, webTestClient);
        
        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

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
        Utils.pushUser(userJson, webTestClient);

        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Directrice'], 'isOrganizer': false}"
            .replace('\'', '"');
        Utils.pushUser(user2Json, webTestClient);

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
        Utils.pushUser(userJson, webTestClient);

        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String userProjectJson = "{ 'userEmail': 'eve.mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur', 'Organizer']}"
                        .replace('\'', '"');

        webTestClient.post().uri("/api/userProjects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userProjectJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testAddUserNoRole() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Directrice'], 'isOrganizer': false}"
            .replace('\'', '"');
        User user = Utils.pushUser(user2Json, webTestClient);

        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String userProjectJson = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  []}"
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
            .jsonPath("$.role[0]").isEqualTo("Non défini");
    }

    /*
     * Get project for which the user is an organizer
     */

    @Test
    public void testGetOrganizerProject() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
             .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);
 
        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Directrice'], 'isOrganizer': false}"
             .replace('\'', '"');
        Utils.pushUser(user2Json, webTestClient);
 
        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);
 
        String userProjectJson = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                 + ", 'roles':  ['Organizer']}"
                         .replace('\'', '"');
 
        webTestClient.post().uri("/api/userProjects")
             .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
             .bodyValue(userProjectJson)
             .exchange();

        webTestClient.get().uri("/api/userProjects/organizer/del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(project.getId())
            .jsonPath("$[0].name").isEqualTo("Christmas show")
            .jsonPath("$[0].description").isEqualTo("Winter show with santa...")
            .jsonPath("$[0].beginningDate").isEqualTo("2020-07-01")
            .jsonPath("$[0].endingDate").isEqualTo(futureEndingDate);

        webTestClient.get().uri("/api/userProjects/organizer/eve.ld@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(project.getId())
            .jsonPath("$[0].name").isEqualTo("Christmas show")
            .jsonPath("$[0].description").isEqualTo("Winter show with santa...")
            .jsonPath("$[0].beginningDate").isEqualTo("2020-07-01")
            .jsonPath("$[0].endingDate").isEqualTo(futureEndingDate);
    }

    @Test
    public void testGetOrganizerProjectEmpty() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
             .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);
 
        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Directrice'], 'isOrganizer': true}"
             .replace('\'', '"');
        Utils.pushUser(user2Json, webTestClient);
 
        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);
 
        String userProjectJson = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                 + ", 'roles':  ['Danseur']}"
                         .replace('\'', '"');
 
        webTestClient.post().uri("/api/userProjects")
             .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
             .bodyValue(userProjectJson)
             .exchange();

        webTestClient.get().uri("/api/userProjects/organizer/eve.ld@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isEmpty();
    }

    @Test
    public void testGetOrganizerNotUser() {
        webTestClient.get().uri("/api/userProjects/organizer/eve.ld@mail.com")
            .exchange()
            .expectStatus().isNotFound();
    }

    /*
     * Get user participating in the project
     */

    @Test
    public void testGetProjectUsers() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Directrice'], 'isOrganizer': false}"
            .replace('\'', '"');
        Utils.pushUser(user2Json, webTestClient);

        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String userProjectJson = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur']}"
                        .replace('\'', '"');

        webTestClient.post().uri("/api/userProjects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userProjectJson)
            .exchange();

        webTestClient.get().uri("/api/userProjects/" + project.getId())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[*].email").value(hasItems("del.vr@mail.com", "eve.ld@mail.com"))
            .jsonPath("$[*].firstName").value(hasItems("Del", "eve"))
            .jsonPath("$[*].lastName").value(hasItems("vr", "ld"));
    }

    @Test
    public void testGetProjectUsersNotFound() {
        webTestClient.get().uri("/api/userProjects/1")
            .exchange()
            .expectStatus().isNotFound();
    }

    /*
     * Get the user roles in the project
     */

    @Test
    public void testGetRoles() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String userProjectJson = "{ 'userEmail': 'del.vr@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur']}"
                        .replace('\'', '"');

        webTestClient.post().uri("/api/userProjects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userProjectJson)
            .exchange();

        webTestClient.get().uri("/api/projects/" + project.getId() + "/users/" + user.getId() + "/roles")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[*]").value(hasItems("Organizer", "Danseur"));
    }

    @Test
    public void testGetRoleProjectNotFound() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        webTestClient.get().uri("/api/projects/0/users/" + user.getId() + "/roles")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void testGetRolesUserNotFound() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        webTestClient.get().uri("/api/projects/" + project.getId() +1 + "/users/" + user.getId() + "/roles")
            .exchange()
            .expectStatus().isNotFound();
    }

    /*
     * Tests delete role
     */

    @Test
    public void testDeleteRole() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Directrice'], 'isOrganizer': false}"
            .replace('\'', '"');
        User user = Utils.pushUser(user2Json, webTestClient);

        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String userProjectJson = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur', 'Organizer']}"
                        .replace('\'', '"');

        webTestClient.post().uri("/api/userProjects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userProjectJson)
            .exchange();

        webTestClient.delete().uri("/api/projects/" + project.getId() + "/users/" + user.getId() + "/roles/Danseur")
            .exchange()
            .expectStatus().isNoContent();

        webTestClient.get().uri("/api/projects/" + project.getId() + "/users/" + user.getId() + "/roles")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[*]").value(hasItems("Organizer"));
    }

    @Test
    public void testDeleteAllRole() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Directrice'], 'isOrganizer': false}"
            .replace('\'', '"');
        User user = Utils.pushUser(user2Json, webTestClient);

        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String userProjectJson = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur', 'Organizer']}"
                        .replace('\'', '"');

        webTestClient.post().uri("/api/userProjects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userProjectJson)
            .exchange();

        webTestClient.delete().uri("/api/projects/" + project.getId() + "/users/" + user.getId() + "/roles/Danseur")
            .exchange()
            .expectStatus().isNoContent();

        webTestClient.delete().uri("/api/projects/" + project.getId() + "/users/" + user.getId() + "/roles/Organizer")
            .exchange()
            .expectStatus().isNoContent();

        webTestClient.get().uri("/api/projects/" + project.getId() + "/users/" + user.getId() + "/roles")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[*]").value(hasItems("Non défini"));
    }

    /*
     * Tests add roles
     */

    @Test
    public void testAddRole() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Directrice'], 'isOrganizer': false}"
            .replace('\'', '"');
        User user = Utils.pushUser(user2Json, webTestClient);

        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String userProjectJson = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur', 'Organizer']}"
                        .replace('\'', '"');

        webTestClient.post().uri("/api/userProjects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userProjectJson)
            .exchange();

        String roles =  "{'roles': ['Metteur en scène', 'Directeur artistique']}"
                .replace('\'', '"');

        webTestClient.post().uri("/api/projects/" + project.getId() + "/users/" + user.getId() + "/roles")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(roles)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.projectId").isEqualTo(project.getId())
            .jsonPath("$.userId").isEqualTo(user.getId())
            .jsonPath("$.role").isArray()
            .jsonPath("$.role[*]").value(hasItems("Metteur en scène", "Directeur artistique"));

        webTestClient.get().uri("/api/projects/" + project.getId() + "/users/" + user.getId() + "/roles")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(4)
            .jsonPath("$[*]").value(hasItems("Organizer", "Danseur", "Metteur en scène", "Directeur artistique"));
    }

    /*
     * Tests remove participant from project
     */

    @Test
    public void testDeleteParticipant() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Directrice'], 'isOrganizer': false}"
            .replace('\'', '"');
        User user = Utils.pushUser(user2Json, webTestClient);

        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String userProjectJson = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur', 'Organizer']}"
                        .replace('\'', '"');

        webTestClient.post().uri("/api/userProjects")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userProjectJson)
            .exchange();

        webTestClient.delete().uri("/api/projects/" + project.getId() + "/users/" + user.getId())
            .exchange()
            .expectStatus().isNoContent();

        webTestClient.get().uri("/api/projects/user/eve.ld@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .json("[]");
    }

    @Test
    public void testDeleteParticipantNotFound() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String futureEndingDate = LocalDate.now().plusDays(1).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '2020-07-01', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        webTestClient.delete().uri("/api/projects/" + project.getId() + "/users/" + user.getId() + 1)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void testDeleteParticipantProjectNotFound() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        webTestClient.delete().uri("/api/projects/0/users/" + user.getId())
            .exchange()
            .expectStatus().isNotFound();
    }

}
