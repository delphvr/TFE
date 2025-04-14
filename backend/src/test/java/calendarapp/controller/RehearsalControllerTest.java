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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;

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
    public void testCreateRehearsal1() {
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
    public void testCreateRehearsal2() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'time': '13:00:00', 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'location': 'Sucrerie de Wavre', 'participantsIds': []}").replace('\'', '"');

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
            .jsonPath("$.projectId").isEqualTo(project.getId())
            .jsonPath("$.time").isEqualTo("13:00:00")
            .jsonPath("$.location").isEqualTo("Sucrerie de Wavre");
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
    public void testCreateRehearsalWrongDate1() {
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
    public void testCreateRehearsalWrongDate2() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String beginningDate =  LocalDate.now().plusDays(3).toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '2025-04-11', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': []}").replace('\'', '"');

        webTestClient.post().uri("/api/rehearsals")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateRehearsalWithParticipant1() {
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

    @Test
    public void testCreateRehearsalWithParticipant2() {
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
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'time': '13:00:00', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');

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
            .jsonPath("$.projectId").isEqualTo(project.getId())
            .jsonPath("$.time").isEqualTo("13:00:00");
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

    @Test
    public void testGetProjectRehearsalsWrongId() {
        webTestClient.get().uri("/api/projects/0/rehearsals")
            .exchange()
            .expectStatus().isNotFound();
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

    @Test
    public void testUpdateRehearsalWithParticipants() {
        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user1 = Utils.pushUser(userJson, webTestClient);

        String userJson2 = "{'firstName': 'Eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user2 = Utils.pushUser(userJson2, webTestClient);
        String user2VacationJson = ("{'email': 'eve.ld@mail.com', 'startDate': '" + beginningDate+ "', 'endDate': '" + futureEndingDate + "'}")
            .replace('\'', '"');
        Utils.pushVacation(user2VacationJson, webTestClient);

        String userJson3 = "{'firstName': 'Jean', 'lastName': 'rv', 'email': 'jean.rv@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user3 = Utils.pushUser(userJson3, webTestClient);
        String user3AvailabilityJson = "{'email': 'jean.rv@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0, 1, 2, 3, 4, 5, 6]}"
            .replace('\'', '"');
        Utils.pushAvailability(user3AvailabilityJson, webTestClient);

        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String userProjectJson1 = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur']}".replace('\'', '"');
        Utils.pushParticipantToProject(userProjectJson1, webTestClient);
        String userProjectJson2 = "{ 'userEmail': 'jean.rv@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur']}".replace('\'', '"');
        Utils.pushParticipantToProject(userProjectJson2, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user1.getId() + "," + user2.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);

        String rehearsalUpdateJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': '"+ rehearsalDate + "', 'time': '14:00:00', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user1.getId() + "," + user3.getId() + "]}").replace('\'', '"');

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
            .jsonPath("$[0].participantsIds.length()").isEqualTo(2)
            .jsonPath("$[0].participantsIds").value(hasItems(user1.getId().intValue(), user3.getId().intValue()));

        webTestClient.get().uri("/api/rehearsals/" + rehearsal.getId() + "/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.present.length()").isEqualTo(1)
            .jsonPath("$.present[0].id").isEqualTo(user3.getId())
            .jsonPath("$.notPresent.length()").isEqualTo(1)
            .jsonPath("$.notPresent[0].id").isEqualTo(user1.getId());
    }

    @Test
    public void testUpdateRehearsalNoDate() {
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

        String rehearsalUpdateJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': null, 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');

        webTestClient.put().uri("/api/rehearsals/" + rehearsal.getId())
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalUpdateJson)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.name").isEqualTo("Little rehearsal")
            .jsonPath("$.description").isEqualTo("Juste check the placements")
            .jsonPath("$.date").isEqualTo(null)
            .jsonPath("$.duration").isEqualTo("PT3H")
            .jsonPath("$.projectId").isEqualTo(project.getId());
    }

    @Test
    public void testUpdateRehearsalWrongDate1() {
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

        String rehearsalNewDate = LocalDate.now().minusDays(3).toString();
        String rehearsalUpdateJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': '"+ rehearsalNewDate +"', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');

        webTestClient.put().uri("/api/rehearsals/" + rehearsal.getId())
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalUpdateJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testUpdateRehearsalWrongDate2() {
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

        String rehearsalNewDate = LocalDate.now().plusDays(31).toString();
        String rehearsalUpdateJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': '"+ rehearsalNewDate +"', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');

        webTestClient.put().uri("/api/rehearsals/" + rehearsal.getId())
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalUpdateJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testUpdateRehearsalWrongId() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();

        String rehearsalUpdateJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': '"+ rehearsalDate + "', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': []}").replace('\'', '"');

        webTestClient.put().uri("/api/rehearsals/0")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalUpdateJson)
            .exchange()
            .expectStatus().isNotFound();
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

    @Test
    public void testGetRehearsalWrongId() {
        webTestClient.get().uri("/api/rehearsals/0")
            .exchange()
            .expectStatus().isNotFound();
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

    /**
     * Test rehearsals precedences
     */

    @Test
    public void testPushRehearsalPrecedence1() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
                .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '"
                + beginningDate + "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}")
                .replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String rehearsalJson1 = ("{'name': 'Small rehearsal', 'description' :'Placements', 'date': null, 'duration': 'PT2H', 'projectId': ' " + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal1 = Utils.pushRehearsal(rehearsalJson1, webTestClient);

        String rehearsalJson2 = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal2 = Utils.pushRehearsal(rehearsalJson2, webTestClient);

        String rehearsalJson3 = ("{'name': 'Rehearsal', 'description' :'A rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': []}").replace('\'', '"');
        Rehearsal rehearsal3 = Utils.pushRehearsal(rehearsalJson3, webTestClient);

        String rehearsalPrecedencesJson1 = ("[" + rehearsal1.getId() + "," + rehearsal3.getId() + "]").replace('\'', '"');

        webTestClient.post().uri("/api/rehearsals/" + rehearsal2.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson1)
            .exchange()
            .expectStatus().isCreated();
    }

    @Test
    public void testPushRehearsalPrecedence2() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
                .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '"
                + beginningDate + "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}")
                .replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String rehearsalJson1 = ("{'name': 'Small rehearsal', 'description' :'Placements', 'date': null, 'duration': 'PT2H', 'projectId': ' " + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal1 = Utils.pushRehearsal(rehearsalJson1, webTestClient);

        String rehearsalJson2 = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal2 = Utils.pushRehearsal(rehearsalJson2, webTestClient);

        String rehearsalJson3 = ("{'name': 'Rehearsal', 'description' :'A rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': []}").replace('\'', '"');
        Rehearsal rehearsal3 = Utils.pushRehearsal(rehearsalJson3, webTestClient);

        String rehearsalPrecedencesJson1 = ("[" + rehearsal1.getId() + "]").replace('\'', '"');
        String rehearsalPrecedencesJson3 = ("[" + rehearsal3.getId() + "]").replace('\'', '"');

        webTestClient.post().uri("/api/rehearsals/" + rehearsal2.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson1)
            .exchange()
            .expectStatus().isCreated();
        webTestClient.post().uri("/api/rehearsals/" + rehearsal1.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson3)
            .exchange()
            .expectStatus().isCreated();
    }

    @Test
    public void testPushWrongRehearsalPrecedence1() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
                .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '"
                + beginningDate + "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}")
                .replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String rehearsalJson1 = ("{'name': 'Small rehearsal', 'description' :'Placements', 'date': null, 'duration': 'PT2H', 'projectId': ' " + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal1 = Utils.pushRehearsal(rehearsalJson1, webTestClient);

        String rehearsalJson2 = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal2 = Utils.pushRehearsal(rehearsalJson2, webTestClient);

        String rehearsalPrecedencesJson1 = ("[" + rehearsal1.getId() + "]").replace('\'', '"');
        String rehearsalPrecedencesJson2 = ("[" + rehearsal2.getId() + "]").replace('\'', '"');

        webTestClient.post().uri("/api/rehearsals/" + rehearsal2.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson1)
            .exchange()
            .expectStatus().isCreated();

        webTestClient.post().uri("/api/rehearsals/" + rehearsal1.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson2)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testPushWrongRehearsalPrecedence2() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
                .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '"
                + beginningDate + "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}")
                .replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String rehearsalJson1 = ("{'name': 'Small rehearsal', 'description' :'Placements', 'date': null, 'duration': 'PT2H', 'projectId': ' " + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal1 = Utils.pushRehearsal(rehearsalJson1, webTestClient);

        String rehearsalJson2 = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': ' "  + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal2 = Utils.pushRehearsal(rehearsalJson2, webTestClient);

        String rehearsalJson3 = ("{'name': 'rehearsal a', 'description' :'Lets all talk about ...', 'date': null, 'duration': 'PT3H', 'projectId': ' "  + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal3 = Utils.pushRehearsal(rehearsalJson3, webTestClient);

        String rehearsalPrecedencesJson1 = ("[" + rehearsal1.getId() + "]").replace('\'', '"');
        String rehearsalPrecedencesJson2 = ("[" + rehearsal2.getId() + "]").replace('\'', '"');
        String rehearsalPrecedencesJson3 = ("[" + rehearsal3.getId() + "]").replace('\'', '"');

        //a->b b->c c->a
        webTestClient.post().uri("/api/rehearsals/" + rehearsal2.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson1)
            .exchange()
            .expectStatus().isCreated();

        webTestClient.post().uri("/api/rehearsals/" + rehearsal3.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson2)
            .exchange()
            .expectStatus().isCreated();

        webTestClient.post().uri("/api/rehearsals/" + rehearsal1.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson3)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testPushWrongRehearsalPrecedenceSameId() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
                .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '"
                + beginningDate + "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}")
                .replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String rehearsalJson1 = ("{'name': 'Small rehearsal', 'description' :'Placements', 'date': null, 'duration': 'PT2H', 'projectId': ' " + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal1 = Utils.pushRehearsal(rehearsalJson1, webTestClient);

        String rehearsalPrecedencesJson1 = ("[" + rehearsal1.getId() + "]").replace('\'', '"');

        webTestClient.post().uri("/api/rehearsals/" + rehearsal1.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson1)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testDeleteRehearsalPrecedence1() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
                .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '"
                + beginningDate + "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}")
                .replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String rehearsalJson1 = ("{'name': 'Small rehearsal', 'description' :'Placements', 'date': null, 'duration': 'PT2H', 'projectId': ' " + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal1 = Utils.pushRehearsal(rehearsalJson1, webTestClient);

        String rehearsalJson2 = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal2 = Utils.pushRehearsal(rehearsalJson2, webTestClient);

        String rehearsalJson3 = ("{'name': 'Rehearsal', 'description' :'A rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': []}").replace('\'', '"');
        Rehearsal rehearsal3 = Utils.pushRehearsal(rehearsalJson3, webTestClient);

        String rehearsalPrecedencesJson1 = ("[" + rehearsal1.getId() + "," + rehearsal3.getId() + "]").replace('\'', '"');

        webTestClient.post().uri("/api/rehearsals/" + rehearsal2.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson1)
            .exchange()
            .expectStatus().isCreated();

        webTestClient.delete().uri(uriBuilder -> uriBuilder
                .path("/api/rehearsals/precedences")
                .queryParam("current", rehearsal2.getId())
                .queryParam("previous", rehearsal3.getId())
                .build())
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    public void testDeleteRehearsalPrecedence2() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
                .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '"
                + beginningDate + "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}")
                .replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String rehearsalJson1 = ("{'name': 'Small rehearsal', 'description' :'Placements', 'date': null, 'duration': 'PT2H', 'projectId': ' " + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal1 = Utils.pushRehearsal(rehearsalJson1, webTestClient);

        String rehearsalJson2 = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal2 = Utils.pushRehearsal(rehearsalJson2, webTestClient);

        String rehearsalJson3 = ("{'name': 'Rehearsal', 'description' :'A rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': []}").replace('\'', '"');
        Rehearsal rehearsal3 = Utils.pushRehearsal(rehearsalJson3, webTestClient);

        String rehearsalPrecedencesJson1 = ("[" + rehearsal1.getId() + "]").replace('\'', '"');
        String rehearsalPrecedencesJson3 = ("[" + rehearsal3.getId() + "]").replace('\'', '"');

        webTestClient.post().uri("/api/rehearsals/" + rehearsal2.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson1)
            .exchange()
            .expectStatus().isCreated();
        webTestClient.post().uri("/api/rehearsals/" + rehearsal1.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson3)
            .exchange()
            .expectStatus().isCreated();

        webTestClient.delete().uri(uriBuilder -> uriBuilder
            .path("/api/rehearsals/precedences")
            .queryParam("current", rehearsal1.getId())
            .queryParam("previous", rehearsal3.getId())
            .build())
        .exchange()
        .expectStatus().isNoContent();

        webTestClient.delete().uri(uriBuilder -> uriBuilder
            .path("/api/rehearsals/precedences")
            .queryParam("current", rehearsal2.getId())
            .queryParam("previous", rehearsal1.getId())
            .build())
        .exchange()
        .expectStatus().isNoContent();
    }

    @Test
    public void testGetRehearsalPrecedence1() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
                .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '"
                + beginningDate + "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}")
                .replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String rehearsalJson1 = ("{'name': 'Small rehearsal', 'description' :'Placements', 'date': null, 'duration': 'PT2H', 'projectId': ' " + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal1 = Utils.pushRehearsal(rehearsalJson1, webTestClient);

        String rehearsalJson2 = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal2 = Utils.pushRehearsal(rehearsalJson2, webTestClient);

        String rehearsalJson3 = ("{'name': 'Rehearsal', 'description' :'A rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': []}").replace('\'', '"');
        Rehearsal rehearsal3 = Utils.pushRehearsal(rehearsalJson3, webTestClient);

        String rehearsalPrecedencesJson1 = ("[" + rehearsal1.getId() + "," + rehearsal3.getId() + "]").replace('\'', '"');

        webTestClient.post().uri("/api/rehearsals/" + rehearsal2.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson1)
            .exchange()
            .expectStatus().isCreated();

        webTestClient.get().uri("/api/rehearsals/" + rehearsal2.getId() + "/precedences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.previous.length()").isEqualTo(2)
            .jsonPath("$.previous[?(@.id == " + rehearsal1.getId() + ")]").exists()
            .jsonPath("$.previous[?(@.id == " + rehearsal3.getId() + ")]").exists()
            .jsonPath("$.following.length()").isEqualTo(0)
            .jsonPath("$.notConstraint.length()").isEqualTo(0)
            .jsonPath("$.constraintByOthers.length()").isEqualTo(0);

        webTestClient.get().uri("/api/rehearsals/" + rehearsal1.getId() + "/precedences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.previous.length()").isEqualTo(0)
            .jsonPath("$.following.length()").isEqualTo(1)
            .jsonPath("$.following[?(@.id == " + rehearsal2.getId() + ")]").exists()
            .jsonPath("$.notConstraint.length()").isEqualTo(1)
            .jsonPath("$.notConstraint[?(@.id == " + rehearsal3.getId() + ")]").exists()
            .jsonPath("$.constraintByOthers.length()").isEqualTo(0);
    }

    @Test
    public void testGetRehearsalPrecedence2() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
                .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '"
                + beginningDate + "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}")
                .replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String rehearsalJson1 = ("{'name': 'Small rehearsal', 'description' :'Placements', 'date': null, 'duration': 'PT2H', 'projectId': ' " + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal1 = Utils.pushRehearsal(rehearsalJson1, webTestClient);

        String rehearsalJson2 = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal2 = Utils.pushRehearsal(rehearsalJson2, webTestClient);

        String rehearsalJson3 = ("{'name': 'Rehearsal', 'description' :'A rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': []}").replace('\'', '"');
        Rehearsal rehearsal3 = Utils.pushRehearsal(rehearsalJson3, webTestClient);

        String rehearsalPrecedencesJson1 = ("[" + rehearsal1.getId() + "]").replace('\'', '"');

        webTestClient.post().uri("/api/rehearsals/" + rehearsal2.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson1)
            .exchange()
            .expectStatus().isCreated();

        webTestClient.get().uri("/api/rehearsals/" + rehearsal2.getId() + "/precedences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.previous.length()").isEqualTo(1)
            .jsonPath("$.previous[?(@.id == " + rehearsal1.getId() + ")]").exists()
            .jsonPath("$.following.length()").isEqualTo(0)
            .jsonPath("$.notConstraint.length()").isEqualTo(1)
            .jsonPath("$.notConstraint[?(@.id == " + rehearsal3.getId() + ")]").exists()
            .jsonPath("$.constraintByOthers.length()").isEqualTo(0);
    }

    @Test
    public void testGetRehearsalPrecedence3() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
                .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '"
                + beginningDate + "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}")
                .replace('\'', '"');
        Project project = Utils.pushProject(projectJson, webTestClient);

        String rehearsalJson1 = ("{'name': 'Small rehearsal', 'description' :'Placements', 'date': null, 'duration': 'PT2H', 'projectId': ' " + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal1 = Utils.pushRehearsal(rehearsalJson1, webTestClient);

        String rehearsalJson2 = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal2 = Utils.pushRehearsal(rehearsalJson2, webTestClient);

        String rehearsalJson3 = ("{'name': 'Rehearsal', 'description' :'A rehearsal with everyone', 'date': null, 'duration': 'PT3H', 'projectId': '" + project.getId() + "', 'participantsIds': []}").replace('\'', '"');
        Rehearsal rehearsal3 = Utils.pushRehearsal(rehearsalJson3, webTestClient);

        String rehearsalPrecedencesJson1 = ("[" + rehearsal1.getId() + "]").replace('\'', '"');
        String rehearsalPrecedencesJson3 = ("[" + rehearsal3.getId() + "]").replace('\'', '"');

        //1->2, 3->1
        webTestClient.post().uri("/api/rehearsals/" + rehearsal2.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson1)
            .exchange()
            .expectStatus().isCreated();
        webTestClient.post().uri("/api/rehearsals/" + rehearsal1.getId() + "/precedences")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(rehearsalPrecedencesJson3)
            .exchange()
            .expectStatus().isCreated();

        webTestClient.get().uri("/api/rehearsals/" + rehearsal2.getId() + "/precedences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.previous.length()").isEqualTo(1)
            .jsonPath("$.previous[?(@.id == " + rehearsal1.getId() + ")]").exists()
            .jsonPath("$.following.length()").isEqualTo(0)
            .jsonPath("$.notConstraint.length()").isEqualTo(0)
            .jsonPath("$.constraintByOthers.length()").isEqualTo(1)
            .jsonPath("$.constraintByOthers[?(@.id == " + rehearsal3.getId() + ")]").exists();
    }

    /*
     * Tests get a user rehearsals
     */

    @Test
    public void testGetUserRehearsal1() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate + "', 'time': '14:00:00', 'location': 'Studio 9', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);
        
        webTestClient.get().uri("/api/users/del.vr@mail.com/rehearsals")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].id").isEqualTo(rehearsal.getId())
            .jsonPath("$[0].name").isEqualTo("General rehearsal")
            .jsonPath("$[0].description").isEqualTo("Last rehearsal with everyone")
            .jsonPath("$[0].date").isEqualTo(rehearsalDate)
            .jsonPath("$[0].time").isEqualTo("14:00:00")
            .jsonPath("$[0].location").isEqualTo("Studio 9")
            .jsonPath("$[0].duration").isEqualTo("PT3H")
            .jsonPath("$[0].projectId").isEqualTo(project.getId());
    }

    @Test
    public void testGetUserRehearsal2() {
        String userJson1 = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user1 = Utils.pushUser(userJson1, webTestClient);
        String userJson2 = "{'firstName': 'Eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user2 = Utils.pushUser(userJson2, webTestClient);
        
        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(15).toString();
        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);
        String userProjectJson1 = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur']}".replace('\'', '"');
        Utils.pushParticipantToProject(userProjectJson1, webTestClient);

        String rehearsalDate1 = LocalDate.now().plusDays(3).toString();
        String rehearsalJson1 = ("{'name': 'General rehearsal', 'description' :'Last rehearsal with everyone', 'date': '"+ rehearsalDate1 + "', 'time': '10:00:00', 'location': 'Theatre', 'duration': 'PT5H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user1.getId() + "," + user2.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal1 = Utils.pushRehearsal(rehearsalJson1, webTestClient);

        String rehearsalDate2 = LocalDate.now().plusDays(1).toString();
        String rehearsalJson2 = ("{'name': 'Small rehearsal 1', 'description' :'Placements', 'date': '"+ rehearsalDate2 + "', 'time': '14:00:00', 'location': 'Studio 9', 'duration': 'PT2H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user1.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal2 = Utils.pushRehearsal(rehearsalJson2, webTestClient);

        String rehearsalJson3 = ("{'name': 'Small rehearsal 2', 'description' :'Placements', 'date': '"+ rehearsalDate2 + "', 'time': '14:30:00', 'location': 'Studio 9', 'duration': 'PT2H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user2.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal3 = Utils.pushRehearsal(rehearsalJson3, webTestClient);
        
        webTestClient.get().uri("/api/users/del.vr@mail.com/rehearsals")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[*].id").value(containsInAnyOrder(rehearsal1.getId().intValue(), rehearsal2.getId().intValue()))
            .jsonPath("$[*].name").value(containsInAnyOrder("General rehearsal", "Small rehearsal 1"))
            .jsonPath("$[*].description").value(containsInAnyOrder("Last rehearsal with everyone", "Placements"))
            .jsonPath("$[*].date").value(containsInAnyOrder(rehearsalDate1, rehearsalDate2))
            .jsonPath("$[*].time").value(containsInAnyOrder("10:00:00", "14:00:00"))
            .jsonPath("$[*].location").value(containsInAnyOrder("Theatre", "Studio 9"))
            .jsonPath("$[*].duration").value(containsInAnyOrder("PT5H", "PT2H"))
            .jsonPath("$[0].projectId").isEqualTo(project.getId())
            .jsonPath("$[1].projectId").isEqualTo(project.getId());

        webTestClient.get().uri("/api/users/eve.ld@mail.com/rehearsals")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[*].id").value(containsInAnyOrder(rehearsal1.getId().intValue(), rehearsal3.getId().intValue()))
            .jsonPath("$[*].name").value(containsInAnyOrder("General rehearsal", "Small rehearsal 2"))
            .jsonPath("$[*].description").value(containsInAnyOrder("Last rehearsal with everyone", "Placements"))
            .jsonPath("$[*].date").value(containsInAnyOrder(rehearsalDate1, rehearsalDate2))
            .jsonPath("$[*].time").value(containsInAnyOrder("10:00:00", "14:30:00"))
            .jsonPath("$[*].location").value(containsInAnyOrder("Theatre", "Studio 9"))
            .jsonPath("$[*].duration").value(containsInAnyOrder("PT5H", "PT2H"))
            .jsonPath("$[0].projectId").isEqualTo(project.getId())
            .jsonPath("$[1].projectId").isEqualTo(project.getId());
    }

    /*
     * Tests get the users presences at a rehearsal
     */

    @Test
    public void testGetRehearsalsPresences1() {
        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user1 = Utils.pushUser(userJson, webTestClient);

        String userJson2 = "{'firstName': 'Eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user2 = Utils.pushUser(userJson2, webTestClient);
        String user2VacationJson = ("{'email': 'eve.ld@mail.com', 'startDate': '" + beginningDate+ "', 'endDate': '" + futureEndingDate + "'}")
            .replace('\'', '"');
        Utils.pushVacation(user2VacationJson, webTestClient);
        String user2AvailabilityJson = "{'email': 'eve.ld@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0, 1, 2, 3, 4, 5, 6]}"
            .replace('\'', '"');
        Utils.pushAvailability(user2AvailabilityJson, webTestClient);

        String userJson3 = "{'firstName': 'Jean', 'lastName': 'rv', 'email': 'jean.rv@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user3 = Utils.pushUser(userJson3, webTestClient);
        String user3AvailabilityJson = "{'email': 'jean.rv@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0, 1, 2, 3, 4, 5, 6]}"
            .replace('\'', '"');
        Utils.pushAvailability(user3AvailabilityJson, webTestClient);

        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String userProjectJson1 = "{ 'userEmail': 'eve.ld@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur']}".replace('\'', '"');
        Utils.pushParticipantToProject(userProjectJson1, webTestClient);
        String userProjectJson2 = "{ 'userEmail': 'jean.rv@mail.com', 'projectId': ".replace('\'', '"') + project.getId()
                + ", 'roles':  ['Danseur']}".replace('\'', '"');
        Utils.pushParticipantToProject(userProjectJson2, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': '"+ rehearsalDate + "', 'time': '14:00:00', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user1.getId() + "," + user3.getId() + "," + user2.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);

        webTestClient.get().uri("/api/users/del.vr@mail.com/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].present").isEqualTo(false)
            .jsonPath("$[0].userId").isEqualTo(user1.getId())
            .jsonPath("$[0].rehearsalId").isEqualTo(rehearsal.getId());

        webTestClient.get().uri("/api/users/eve.ld@mail.com/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].present").isEqualTo(false)
            .jsonPath("$[0].userId").isEqualTo(user2.getId())
            .jsonPath("$[0].rehearsalId").isEqualTo(rehearsal.getId());

        webTestClient.get().uri("/api/users/jean.rv@mail.com/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].present").isEqualTo(true)
            .jsonPath("$[0].userId").isEqualTo(user3.getId())
            .jsonPath("$[0].rehearsalId").isEqualTo(rehearsal.getId());
    }

    @Test
    public void testGetRehearsalsPresences2() {
        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user1 = Utils.pushUser(userJson, webTestClient);
        String user1AvailabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0, 1, 2, 3, 4, 5, 6]}".replace('\'', '"');
        Utils.pushAvailability(user1AvailabilityJson, webTestClient);

        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String projectJson2 = ("{ 'name': 'Project 2', 'description': 'tt', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project2 =  Utils.pushProject(projectJson2, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson2 = ("{'name': 'Rehearsal 2', 'description' :'tt', 'date': '"+ rehearsalDate + "', 'time': '14:00:00', 'duration': 'PT3H', 'projectId': ' "+ project2.getId() + "', 'participantsIds': [" + user1.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal2 = Utils.pushRehearsal(rehearsalJson2, webTestClient);

        String rehearsalJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': '"+ rehearsalDate + "', 'time': '14:00:00', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user1.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);

        webTestClient.get().uri("/api/users/del.vr@mail.com/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[*].present").value(containsInAnyOrder(true, false))
            .jsonPath("$[0].userId").isEqualTo(user1.getId())
            .jsonPath("$[1].userId").isEqualTo(user1.getId())
            .jsonPath("$[*].rehearsalId").value(containsInAnyOrder(rehearsal.getId().intValue(), rehearsal2.getId().intValue()));

        webTestClient.get().uri("/api/rehearsals/" + rehearsal.getId() + "/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.present.length()").isEqualTo(0)
            .jsonPath("$.notPresent.length()").isEqualTo(1)
            .jsonPath("$.notPresent[0].id").isEqualTo(user1.getId());

        webTestClient.get().uri("/api/rehearsals/" + rehearsal2.getId() + "/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.present.length()").isEqualTo(1)
            .jsonPath("$.present[0].id").isEqualTo(user1.getId())
            .jsonPath("$.notPresent.length()").isEqualTo(0);
        
    }

    @Test
    public void testGetRehearsalsPresences3() {
        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user1 = Utils.pushUser(userJson, webTestClient);
        String user1AvailabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0, 1, 2, 3, 4, 5, 6]}".replace('\'', '"');
        Utils.pushAvailability(user1AvailabilityJson, webTestClient);

        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String projectJson2 = ("{ 'name': 'Project 2', 'description': 'tt', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project2 =  Utils.pushProject(projectJson2, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(3).toString();
        String rehearsalJson2 = ("{'name': 'Rehearsal 2', 'description' :'tt', 'date': '"+ rehearsalDate + "', 'time': '14:00:00', 'duration': 'PT3H', 'projectId': ' "+ project2.getId() + "', 'participantsIds': [" + user1.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal2 = Utils.pushRehearsal(rehearsalJson2, webTestClient);

        String rehearsalJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': '"+ rehearsalDate + "', 'time': '17:00:00', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user1.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);

        webTestClient.get().uri("/api/users/del.vr@mail.com/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[0].present").isEqualTo(true)
            .jsonPath("$[1].present").isEqualTo(true)
            .jsonPath("$[0].userId").isEqualTo(user1.getId())
            .jsonPath("$[1].userId").isEqualTo(user1.getId())
            .jsonPath("$[*].rehearsalId").value(containsInAnyOrder(rehearsal.getId().intValue(), rehearsal2.getId().intValue()));
    }

    @Test
    public void testGetRehearsalsPresences4() {
        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user1 = Utils.pushUser(userJson, webTestClient);
        String user1AvailabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0, 1, 2, 3, 4, 5, 6]}".replace('\'', '"');
        Utils.pushAvailability(user1AvailabilityJson, webTestClient);

        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String projectJson2 = ("{ 'name': 'Project 2', 'description': 'tt', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project2 =  Utils.pushProject(projectJson2, webTestClient);

        String rehearsalDate2 = LocalDate.now().plusDays(3).toString();
        String rehearsalJson2 = ("{'name': 'Rehearsal 2', 'description' :'tt', 'date': '"+ rehearsalDate2 + "', 'time': '14:00:00', 'duration': 'PT3H', 'projectId': ' "+ project2.getId() + "', 'participantsIds': [" + user1.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal2 = Utils.pushRehearsal(rehearsalJson2, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(4).toString();
        String rehearsalJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': '"+ rehearsalDate + "', 'time': '14:00:00', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user1.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);

        webTestClient.get().uri("/api/users/del.vr@mail.com/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[0].present").isEqualTo(true)
            .jsonPath("$[1].present").isEqualTo(true)
            .jsonPath("$[0].userId").isEqualTo(user1.getId())
            .jsonPath("$[1].userId").isEqualTo(user1.getId())
            .jsonPath("$[*].rehearsalId").value(containsInAnyOrder(rehearsal.getId().intValue(), rehearsal2.getId().intValue()));
    }

    /**
     * Test update rehearsal presences
     */

    @Test
    public void testPutRehearsalsPresences1() {
        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user1 = Utils.pushUser(userJson, webTestClient);
        String user1AvailabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0, 1, 2, 3, 4, 5, 6]}".replace('\'', '"');
        Utils.pushAvailability(user1AvailabilityJson, webTestClient);

        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(4).toString();
        String rehearsalJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': '"+ rehearsalDate + "', 'time': '14:00:00', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user1.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);

        webTestClient.put().uri("/api/rehearsals/" + rehearsal.getId() + "/users/del.vr@mail.com/presences?presence=false")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.rehearsalId").isEqualTo(rehearsal.getId())
            .jsonPath("$.present").isEqualTo(false)
            .jsonPath("$.userId").isEqualTo(user1.getId());

        webTestClient.get().uri("/api/rehearsals/" + rehearsal.getId() + "/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.present.length()").isEqualTo(0)
            .jsonPath("$.notPresent.length()").isEqualTo(1)
            .jsonPath("$.notPresent[0].id").isEqualTo(user1.getId());
    }

    @Test
    public void testPutRehearsalsPresences2() {
        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user1 = Utils.pushUser(userJson, webTestClient);
        String user1AvailabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0, 1, 2, 3, 4, 5, 6]}".replace('\'', '"');
        Utils.pushAvailability(user1AvailabilityJson, webTestClient);
        String userVacationJson = ("{'email': 'del.vr@mail.com', 'startDate': '" + beginningDate+ "', 'endDate': '" + futureEndingDate + "'}")
            .replace('\'', '"');
        Utils.pushVacation(userVacationJson, webTestClient);


        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(4).toString();
        String rehearsalJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': '"+ rehearsalDate + "', 'time': '14:00:00', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user1.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);

        webTestClient.put().uri("/api/rehearsals/" + rehearsal.getId() + "/users/del.vr@mail.com/presences?presence=true")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.rehearsalId").isEqualTo(rehearsal.getId())
            .jsonPath("$.present").isEqualTo(true)
            .jsonPath("$.userId").isEqualTo(user1.getId());

        webTestClient.get().uri("/api/rehearsals/" + rehearsal.getId() + "/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.notPresent.length()").isEqualTo(0)
            .jsonPath("$.present.length()").isEqualTo(1)
            .jsonPath("$.present[0].id").isEqualTo(user1.getId());
    }

    @Test
    public void testPutRehearsalsPresences3() {
        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user1 = Utils.pushUser(userJson, webTestClient);
        String userVacationJson = ("{'email': 'del.vr@mail.com', 'startDate': '" + beginningDate+ "', 'endDate': '" + futureEndingDate + "'}")
            .replace('\'', '"');
        Utils.pushVacation(userVacationJson, webTestClient);


        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(4).toString();
        String rehearsalJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': '"+ rehearsalDate + "', 'time': '14:00:00', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user1.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);

        webTestClient.put().uri("/api/rehearsals/" + rehearsal.getId() + "/users/del.vr@mail.com/presences?presence=false")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.rehearsalId").isEqualTo(rehearsal.getId())
            .jsonPath("$.present").isEqualTo(false)
            .jsonPath("$.userId").isEqualTo(user1.getId());

        webTestClient.get().uri("/api/rehearsals/" + rehearsal.getId() + "/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.present.length()").isEqualTo(0)
            .jsonPath("$.notPresent.length()").isEqualTo(1)
            .jsonPath("$.notPresent[0].id").isEqualTo(user1.getId());
    }

    @Test
    public void testPutRehearsalsPresencesWrongEmail() {
        String beginningDate = LocalDate.now().toString();
        String futureEndingDate = LocalDate.now().plusDays(30).toString();
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user1 = Utils.pushUser(userJson, webTestClient);

        String projectJson = ("{ 'name': 'Christmas show', 'description': 'Winter show with santa...', 'beginningDate': '" + beginningDate+ "', 'endingDate': '" + futureEndingDate + "', 'organizerEmail': 'del.vr@mail.com'}").replace('\'', '"');
        Project project =  Utils.pushProject(projectJson, webTestClient);

        String rehearsalDate = LocalDate.now().plusDays(4).toString();
        String rehearsalJson = ("{'name': 'Little rehearsal', 'description' :'Juste check the placements', 'date': '"+ rehearsalDate + "', 'time': '14:00:00', 'duration': 'PT3H', 'projectId': ' "+ project.getId() + "', 'participantsIds': [" + user1.getId() + "]}").replace('\'', '"');
        Rehearsal rehearsal = Utils.pushRehearsal(rehearsalJson, webTestClient);

        webTestClient.put().uri("/api/rehearsals/" + rehearsal.getId() + "/users/null/presences?presence=false")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void testPutRehearsalsPresencesWrongId() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        webTestClient.put().uri("/api/rehearsals/0/users/del.vr@mail.com/presences?presence=true")
            .exchange()
            .expectStatus().isNotFound();
    }
    

}
