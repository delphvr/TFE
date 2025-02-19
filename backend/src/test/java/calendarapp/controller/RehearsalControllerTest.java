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
import calendarapp.model.User;
import calendarapp.repository.ProjectRepository;
import calendarapp.repository.RehearsalRepository;
import calendarapp.repository.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test") 
public class RehearsalControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RehearsalRepository rehearsalRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void cleanUpDatabase() {
        rehearsalRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();;
    }

    /*
     * Tests post of a rehearsal
     */

    @Test
    public void testCreateRehearsal() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': []}").replace('\'', '"');

        webTestClient.post().uri("/api/rehearsals")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.date").isEqualTo(rehearsalDate)
            .jsonPath("$.duration").isEqualTo("PT3H")
            .jsonPath("$.projectId").isEqualTo(project.getId());
    }

    @Test
    public void testCreateRehearsalProjectNotFound() {
        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'duration': 'PT3H', 'projectId': '0', 'participantsIds': []}").replace('\'', '"');

        webTestClient.post().uri("/api/rehearsals")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalJson)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void testCreateRehearsalWrongDate() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String beginningDate =  LocalDate.now().plusDays(3).toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().toString();
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': []}").replace('\'', '"');

        webTestClient.post().uri("/api/rehearsals")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateRehearsalWithParticipant() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Directrice'], 'isOrganizer': false}"
            .replace('\'', '"');
        User user = Utils.pushUser(user2Json, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');

        webTestClient.post().uri("/api/rehearsals")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.date").isEqualTo(rehearsalDate)
            .jsonPath("$.duration").isEqualTo("PT3H")
            .jsonPath("$.projectId").isEqualTo(project.getId());
    }

}
