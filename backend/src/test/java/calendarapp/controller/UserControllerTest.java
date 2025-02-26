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

import calendarapp.Utils;
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
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
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
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'wrong', 'professions': ['Danseur']}"
                .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateUserMailNull() throws Exception {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': null, 'professions': ['Danseur']}"
                .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateUserMailBlank() throws Exception {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': '', 'professions': ['Danseur']}"
                .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateUserMailConflit() throws Exception {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'd@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange();

        String userJsonbis = "{'firstName': 'F', 'lastName': 'l', 'email': 'd@mail.com', 'professions': ['Danseur']}"
                .replace('\'', '"');

        webTestClient.post().uri("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(userJsonbis)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    /*
     * Tests delete user based on id
     */

    @Test
    public void testDeleteUser() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
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
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
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
     * Tests get user with id
     */

    @Test
    public void testGetUser() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}".replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        webTestClient.get().uri("/api/users/" + user.getId())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.firstName").isEqualTo("Del")
            .jsonPath("$.lastName").isEqualTo("vr")
            .jsonPath("$.email").isEqualTo("del.vr@mail.com");
    }

    @Test
    public void testGetUserNotFound() {
        webTestClient.get().uri("/api/users/0")
            .exchange()
            .expectStatus().isNotFound();
    }

    /*
     * Tests get user with email
     */

    @Test
    public void testGetUserWithEmail() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}".replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        webTestClient.get().uri("/api/users?email=" + user.getEmail())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.firstName").isEqualTo("Del")
            .jsonPath("$.lastName").isEqualTo("vr")
            .jsonPath("$.email").isEqualTo("del.vr@mail.com");
    }

    @Test
    public void testGetUserWithEmailNotFound() {
        webTestClient.get().uri("/api/users?email=b@b.com")
            .exchange()
            .expectStatus().isNotFound();
    }

    /*
     * Tests get user professions
     */

    @Test
    public void testGetUserProfessions() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}".replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        webTestClient.get().uri("/api/users/" + user.getEmail() +"/professions")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .json("['Danseur']");
    }

    /*
     * Tests update a user //TODO test changing the professions
     */
    @Test
    public void testUpdateUser() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}".replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String updatedUserJson = "{'firstName': 'Deli', 'lastName': 'vr', 'email': 'deli.vr@mail.com', 'professions': ['Danseur']}".replace('\'', '"');

        webTestClient.put().uri("/api/users/" + user.getId())
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(updatedUserJson)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.firstName").isEqualTo("Deli")
            .jsonPath("$.lastName").isEqualTo("vr")
            .jsonPath("$.email").isEqualTo("deli.vr@mail.com");
    }

}
