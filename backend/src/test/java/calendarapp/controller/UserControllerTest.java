package calendarapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import calendarapp.model.User;
import calendarapp.repository.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test") 
public class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void cleanUpDatabase() {
        userRepository.deleteAll();
    }

    /*
     * Tests post creation users
     */

    @Test
    public void testCreateUser() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.firstName").isEqualTo("Del")
            .jsonPath("$.lastName").isEqualTo("vr")
            .jsonPath("$.email").isEqualTo("del.vr@mail.com");
    }

    @Test
    public void testCreateUserWrongMail() throws Exception {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'wrong', 'professions': ['Danseur'], 'isOrganizer': true}"
                .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateUserMailNull() throws Exception {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': null, 'professions': ['Danseur'], 'isOrganizer': true}"
                .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateUserMailBlank() throws Exception {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': '', 'professions': ['Danseur'], 'isOrganizer': true}"
                .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateUserMailConflit() throws Exception {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'd@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange();

        String userJsonbis = "{'firstName': 'F', 'lastName': 'l', 'email': 'd@mail.com', 'professions': ['Danseur'], 'isOrganizer': false}"
                .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJsonbis)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    /*
     * Test isOrganizer based on user email
     */

    @Test
    public void testIsOrganizerT() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
                .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange();

        webTestClient.get().uri("/api/users/organizer/del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    public void testIsOrganizerNotFound() {
        webTestClient.get().uri("/api/users/organizer/del.vr@mail.com")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void testIsOrganizerF() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': false}"
                .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange();

        webTestClient.get().uri("/api/users/organizer/del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody(Boolean.class).isEqualTo(false);
    }

    /*
     * Tests delete user based on id
     */

    @Test
    public void testDeleteUser() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
                .replace('\'', '"');

        User user = webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectBody(User.class)
            .returnResult()
            .getResponseBody();

        webTestClient.delete().uri("/api/users/" + user.getId())
            .exchange()
            .expectStatus().isNoContent();

        webTestClient.get().uri("/api/users/" + user.getId())
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void testDeleteUserNotFound() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur'], 'isOrganizer': true}"
                .replace('\'', '"');

        User user = webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectBody(User.class)
            .returnResult()
            .getResponseBody();

        webTestClient.delete().uri("/api/users/" + (user.getId()+1))
            .exchange()
            .expectStatus().isNotFound();
    }

    /*
     * Pour tester le get ? : 
     * List<User> users = webTestClient.get().uri("/api/users")
            .exchange()
            .expectBody(new ParameterizedTypeReference<List<User>>() {})
            .returnResult()
            .getResponseBody();
        System.out.println("USER : " + users);
     */

}
