package calendarapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.hamcrest.Matchers.containsInAnyOrder;

import calendarapp.Utils;
import calendarapp.model.User;
import calendarapp.repository.UserRepository;
import calendarapp.repository.WeeklyAvailabilityRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test") 
public class WeeklyAvailabilityControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private WeeklyAvailabilityRepository weeklyAvailabilityRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void cleanUpDatabase() {
        weeklyAvailabilityRepository.deleteAll();
        userRepository.deleteAll();;
    }

    /*
     * Tests post of an availability
     */

    @Test
    public void testCreateAvailability1() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String availabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0, 1, 2, 3, 4, 5, 6]}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/availabilities")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(availabilityJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(7)
            .jsonPath("$[0].startTime").isEqualTo("08:00:00")
            .jsonPath("$[0].endTime").isEqualTo("23:00:00")
            .jsonPath("$[*].weekday").value(containsInAnyOrder(0, 1, 2, 3, 4, 5, 6));
    }

    @Test
    public void testCreateAvailability2() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String availabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '18:00:00', 'endTime': '23:00:00', 'weekdays': [3, 4, 5, 6]}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/availabilities")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(availabilityJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(4)
            .jsonPath("$[0].startTime").isEqualTo("18:00:00")
            .jsonPath("$[0].endTime").isEqualTo("23:00:00")
            .jsonPath("$[*].weekday").value(containsInAnyOrder(3, 4, 5, 6));

        availabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '10:00:00', 'endTime': '15:00:00', 'weekdays': [1, 4, 6]}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/availabilities")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(availabilityJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[0].startTime").isEqualTo("10:00:00")
            .jsonPath("$[0].endTime").isEqualTo("15:00:00")
            .jsonPath("$[1].startTime").isEqualTo("10:00:00")
            .jsonPath("$[1].endTime").isEqualTo("15:00:00")
            .jsonPath("$[2].startTime").isEqualTo("10:00:00")
            .jsonPath("$[2].endTime").isEqualTo("15:00:00")
            .jsonPath("$[*].weekday").value(containsInAnyOrder(1, 4, 6));
    }

    @Test
    public void testCreateAvailability3() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String availabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '15:00:00', 'endTime': '16:30:00', 'weekdays': [2]}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/availabilities")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(availabilityJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].startTime").isEqualTo("15:00:00")
            .jsonPath("$[0].endTime").isEqualTo("16:30:00")
            .jsonPath("$[0].weekday").isEqualTo(2);
    }

    @Test
    public void testCreateAvailabilityWrongEmail() {
        String availabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0]}"
            .replace('\'', '"');
        webTestClient.post().uri("/api/availabilities")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(availabilityJson)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void testCreateAvailabilityWrongTime() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String availabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '18:00:00', 'endTime': '08:00:00', 'weekdays': [0]}"
            .replace('\'', '"');

        webTestClient.post().uri("/api/availabilities")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(availabilityJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateAvailabilityOverlap() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String availabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0, 1, 2, 3, 4, 5, 6]}"
            .replace('\'', '"');
        Utils.pushAvailability(availabilityJson, webTestClient);

        availabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '10:00:00', 'endTime': '13:00:00', 'weekdays': [1]}"
            .replace('\'', '"');
            webTestClient.post().uri("/api/availabilities")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(availabilityJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    /*
     * Test get user availabilities
     */
    
    @Test
    public void testGetUserAvailability1() {
        String userJson1 = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson1, webTestClient);

        String userJson2 = "{'firstName': 'Eve', 'lastName': 'pl', 'email': 'eve.pl@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson2, webTestClient);

        String availabilityJson1 = "{'email': 'del.vr@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0, 1, 2]}"
            .replace('\'', '"');
        Utils.pushAvailability(availabilityJson1, webTestClient);

        String availabilityJson2 = "{'email': 'eve.pl@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0, 1, 2]}"
            .replace('\'', '"');
        Utils.pushAvailability(availabilityJson2, webTestClient);

        webTestClient.get().uri("/api/users/availabilities?email=del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[0].startTime").isEqualTo("08:00:00")
            .jsonPath("$[0].endTime").isEqualTo("23:00:00")
            .jsonPath("$[1].startTime").isEqualTo("08:00:00")
            .jsonPath("$[1].endTime").isEqualTo("23:00:00")
            .jsonPath("$[2].startTime").isEqualTo("08:00:00")
            .jsonPath("$[2].endTime").isEqualTo("23:00:00")
            .jsonPath("$[*].weekday").value(containsInAnyOrder(0, 1, 2));

        webTestClient.get().uri("/api/users/availabilities?email=eve.pl@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[0].startTime").isEqualTo("08:00:00")
            .jsonPath("$[0].endTime").isEqualTo("23:00:00")
            .jsonPath("$[1].startTime").isEqualTo("08:00:00")
            .jsonPath("$[1].endTime").isEqualTo("23:00:00")
            .jsonPath("$[2].startTime").isEqualTo("08:00:00")
            .jsonPath("$[2].endTime").isEqualTo("23:00:00")
            .jsonPath("$[*].weekday").value(containsInAnyOrder(0, 1, 2));            
    }

    @Test
    public void testGetUserAvailability2() {
        String userJson1 = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson1, webTestClient);

        String userJson2 = "{'firstName': 'Eve', 'lastName': 'pl', 'email': 'eve.pl@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson2, webTestClient);

        String availabilityJson1 = "{'email': 'del.vr@mail.com', 'startTime': '10:00:00', 'endTime': '13:00:00', 'weekdays': [1]}"
            .replace('\'', '"');
        Utils.pushAvailability(availabilityJson1, webTestClient);

        String availabilityJson2 = "{'email': 'eve.pl@mail.com', 'startTime': '15:00:00', 'endTime': '18:00:00', 'weekdays': [1, 3, 5]}"
            .replace('\'', '"');
        Utils.pushAvailability(availabilityJson2, webTestClient);

        webTestClient.get().uri("/api/users/availabilities?email=del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].startTime").isEqualTo("10:00:00")
            .jsonPath("$[0].endTime").isEqualTo("13:00:00")
            .jsonPath("$[*].weekday").isEqualTo(1);

        webTestClient.get().uri("/api/users/availabilities?email=eve.pl@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[0].startTime").isEqualTo("15:00:00")
            .jsonPath("$[0].endTime").isEqualTo("18:00:00")
            .jsonPath("$[1].startTime").isEqualTo("15:00:00")
            .jsonPath("$[1].endTime").isEqualTo("18:00:00")
            .jsonPath("$[2].startTime").isEqualTo("15:00:00")
            .jsonPath("$[2].endTime").isEqualTo("18:00:00")
            .jsonPath("$[*].weekday").value(containsInAnyOrder(1, 3, 5));            
    }

    /*
     * Test delete an availability
     */
    
    @Test
    public void testDeleteAvailability() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String availabilityJson = "{'email': 'del.vr@mail.com', 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekdays': [0, 1, 2]}"
            .replace('\'', '"');
        Utils.pushAvailability(availabilityJson, webTestClient);

        String deleteAvailabilityJson = ("{'userId': " + user.getId() + ", 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekday': 0}")
            .replace('\'', '"');
        webTestClient.method(HttpMethod.DELETE).uri("/api/availabilities")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(deleteAvailabilityJson)
            .exchange()
            .expectStatus().isNoContent();
        webTestClient.get().uri("/api/users/availabilities?email=del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[0].startTime").isEqualTo("08:00:00")
            .jsonPath("$[0].endTime").isEqualTo("23:00:00")
            .jsonPath("$[1].startTime").isEqualTo("08:00:00")
            .jsonPath("$[1].endTime").isEqualTo("23:00:00")
            .jsonPath("$[*].weekday").value(containsInAnyOrder(1, 2));  
    }

    @Test
    public void testDeleteAvailabilityUserIdNull() {
        String deleteAvailabilityJson = "{'userId': null, 'startTime': '08:00:00', 'endTime': '23:00:00', 'weekday': 0}"
            .replace('\'', '"');
        webTestClient.method(HttpMethod.DELETE).uri("/api/availabilities")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(deleteAvailabilityJson)
            .exchange()
            .expectStatus().isNotFound();
    }
}
