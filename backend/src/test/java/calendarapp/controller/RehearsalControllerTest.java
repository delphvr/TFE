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
import calendarapp.model.Rehearsal;
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
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
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
            .jsonPath("$.name").isEqualTo("General rehearsal")
            .jsonPath("$.description").isEqualTo("Last rehearsal with everyone")
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
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
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
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String user2Json = "{'firstName': 'eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Costumier']}"
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
            .jsonPath("$.name").isEqualTo("General rehearsal")
            .jsonPath("$.description").isEqualTo("Last rehearsal with everyone")
            .jsonPath("$.date").isEqualTo(rehearsalDate)
            .jsonPath("$.duration").isEqualTo("PT3H")
            .jsonPath("$.projectId").isEqualTo(project.getId());
    }

    /*
     * Tests get the rehearsals of a project
     */
    @Test
    public void testGetProjectRehearsals() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);
        
        webTestClient.get().uri("/api/projects/"+ project.getId() + "/rehearsals")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(rehearsal.getId())
            .jsonPath("$[0].name").isEqualTo("General rehearsal")
            .jsonPath("$[0].description").isEqualTo("Last rehearsal with everyone")
            .jsonPath("$[0].date").isEqualTo(rehearsalDate)
            .jsonPath("$[0].duration").isEqualTo("PT3H")
            .jsonPath("$[0].projectId").isEqualTo(project.getId())
            .jsonPath("$[0].participantsIds[0]").isEqualTo(user.getId());
    }

    /*
     * Tests delete a rehearsals
     */
    @Test
    public void testDeleteRehearsal() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);

        webTestClient.delete().uri("/api/rehearsals/" + rehearsal.getId())
            .exchange()
            .expectStatus().isNoContent();
        
        webTestClient.get().uri("/api/projects/"+ project.getId() + "/rehearsals")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .json("[]");
    }

    /*
     * Tests get the user object of a rehearsal
     */
    @Test
    public void testGetRehearsalParticipants() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);
        
        webTestClient.get().uri("/api/rehearsals/" + rehearsal.getId() + "/participants")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(user.getId())
            .jsonPath("$[0].firstName").isEqualTo(user.getFirstName())
            .jsonPath("$[0].lastName").isEqualTo(user.getLastName())
            .jsonPath("$[0].email").isEqualTo(user.getEmail());
    }

    /*
     * Tests update a rehearsal
     */
    @Test
    public void testUpdateRehearsal() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);

        String rehearsalUpdateJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': '"+ rehearsalDate + "', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');

        webTestClient.put().uri("/api/rehearsals/" + rehearsal.getId())
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalUpdateJson)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.name").isEqualTo("Little rehearsal")
            .jsonPath("$.description").isEqualTo("Juste check the placements")
            .jsonPath("$.date").isEqualTo(rehearsalDate)
            .jsonPath("$.duration").isEqualTo("PT3H")
            .jsonPath("$.projectId").isEqualTo(project.getId());
        
        webTestClient.get().uri("/api/projects/"+ project.getId() + "/rehearsals")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(rehearsal.getId())
            .jsonPath("$[0].name").isEqualTo("Little rehearsal")
            .jsonPath("$[0].description").isEqualTo("Juste check the placements")
            .jsonPath("$[0].date").isEqualTo(rehearsalDate)
            .jsonPath("$[0].duration").isEqualTo("PT3H")
            .jsonPath("$[0].projectId").isEqualTo(project.getId())
            .jsonPath("$[0].participantsIds[0]").isEqualTo(user.getId());
    }

    /*
     * Tests get a rehearsal with id
     */
    @Test
    public void testGetRehearsal() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);
        
        webTestClient.get().uri("/api/rehearsals/" + rehearsal.getId())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(rehearsal.getId())
            .jsonPath("$.name").isEqualTo("General rehearsal")
            .jsonPath("$.description").isEqualTo("Last rehearsal with everyone")
            .jsonPath("$.date").isEqualTo(rehearsalDate)
            .jsonPath("$.duration").isEqualTo("PT3H")
            .jsonPath("$.projectId").isEqualTo(project.getId());
    }

     /*
     * Tests get user rehearsals on project //TODO tests with several project and rehearsals
     */
    @Test
    public void testGetUserRehearsalOnProject() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);
        
        webTestClient.get().uri("/api/users/" + user.getEmail() + "/projects/"+ project.getId() + "/rehearsals")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(rehearsal.getId())
            .jsonPath("$[0].name").isEqualTo("General rehearsal")
            .jsonPath("$[0].description").isEqualTo("Last rehearsal with everyone")
            .jsonPath("$[0].date").isEqualTo(rehearsalDate)
            .jsonPath("$[0].duration").isEqualTo("PT3H")
            .jsonPath("$[0].projectId").isEqualTo(project.getId());
    }

}
