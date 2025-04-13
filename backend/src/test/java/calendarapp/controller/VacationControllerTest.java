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

import java.time.LocalDate;

import calendarapp.Utils;
import calendarapp.model.User;
import calendarapp.repository.UserRepository;
import calendarapp.repository.VacationRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test") 
public class VacationControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private VacationRepository vacationRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void cleanUpDatabase() {
        vacationRepository.deleteAll();
        userRepository.deleteAll();;
    }

    /*
     * Tests post of a vacation
     */

    @Test
    public void testCreateVacation1() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(3).toString();
        String vacationJson = ("{'email': 'del.vr@mail.com', 'startDate': '" + startDate + "' , 'endDate': '" + endDate + "'}")
            .replace('\'', '"');

        webTestClient.post().uri("/api/vacations")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(vacationJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.userId").isEqualTo(user.getId())
            .jsonPath("$.startDate").isEqualTo(startDate)
            .jsonPath("$.endDate").isEqualTo(endDate);
    }

    @Test
    public void testCreateVacation2() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String startDate1 = LocalDate.now().toString();
        String endDate1 = LocalDate.now().plusDays(3).toString();
        String vacationJson1 = ("{'email': 'del.vr@mail.com', 'startDate': '" + startDate1 + "' , 'endDate': '" + endDate1 + "'}")
            .replace('\'', '"');

        String startDate2 = LocalDate.now().plusDays(5).toString();
        String endDate2 = LocalDate.now().plusDays(10).toString();
        String vacationJson2 = ("{'email': 'del.vr@mail.com', 'startDate': '" + startDate2 + "' , 'endDate': '" + endDate2 + "'}")
            .replace('\'', '"');

        webTestClient.post().uri("/api/vacations")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(vacationJson1)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.userId").isEqualTo(user.getId())
            .jsonPath("$.startDate").isEqualTo(startDate1)
            .jsonPath("$.endDate").isEqualTo(endDate1);

        webTestClient.post().uri("/api/vacations")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(vacationJson2)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.userId").isEqualTo(user.getId())
            .jsonPath("$.startDate").isEqualTo(startDate2)
            .jsonPath("$.endDate").isEqualTo(endDate2);
    }

    @Test
    public void testCreateVacation3() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String startDate = LocalDate.now().plusDays(1).toString();
        String endDate = LocalDate.now().plusDays(1).toString();
        String vacationJson = ("{'email': 'del.vr@mail.com', 'startDate': '" + startDate + "' , 'endDate': '" + endDate + "'}")
            .replace('\'', '"');

        webTestClient.post().uri("/api/vacations")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(vacationJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.userId").isEqualTo(user.getId())
            .jsonPath("$.startDate").isEqualTo(startDate)
            .jsonPath("$.endDate").isEqualTo(endDate);
    }

    @Test
    public void testCreateVacationWrongEndDate() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String startDate1 = LocalDate.now().minusDays(3).toString();
        String endDate1 = LocalDate.now().minusDays(1).toString();
        String VacationJson1 = ("{'email': 'del.vr@mail.com', 'startDate': '" + startDate1 + "' , 'endDate': '" + endDate1 + "'}")
            .replace('\'', '"');

        webTestClient.post().uri("/api/vacations")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(VacationJson1)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateVacationWronDates() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        Utils.pushUser(userJson, webTestClient);

        String startDate = LocalDate.now().plusDays(3).toString();
        String endDate = LocalDate.now().plusDays(1).toString();
        String vacationJson = ("{'email': 'del.vr@mail.com', 'startDate': '" + startDate + "' , 'endDate': '" + endDate + "'}")
            .replace('\'', '"');

        webTestClient.post().uri("/api/vacations")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(vacationJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    /**
     * Test get a user vacation
     */

    @Test
    public void testGetVacation1() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String startDate1 = LocalDate.now().toString();
        String endDate1 = LocalDate.now().plusDays(3).toString();
        String vacationJson1 = ("{'email': 'del.vr@mail.com', 'startDate': '" + startDate1 + "' , 'endDate': '" + endDate1 + "'}")
            .replace('\'', '"');
        Utils.pushVacation(vacationJson1, webTestClient);    

        String startDate2 = LocalDate.now().plusDays(5).toString();
        String endDate2 = LocalDate.now().plusDays(10).toString();
        String vacationJson2 = ("{'email': 'del.vr@mail.com', 'startDate': '" + startDate2 + "' , 'endDate': '" + endDate2 + "'}")
            .replace('\'', '"');
        Utils.pushVacation(vacationJson2, webTestClient);    

        webTestClient.get().uri("/api/users/vacations?email=del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[*].startDate").value(containsInAnyOrder(startDate1, startDate2))
            .jsonPath("$[*].endDate").value(containsInAnyOrder(endDate1, endDate2))
            .jsonPath("$[0].userId").isEqualTo(user.getId())
            .jsonPath("$[1].userId").isEqualTo(user.getId());
    }

    @Test
    public void testGetVacation2() {
        String userJson1 = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user1 = Utils.pushUser(userJson1, webTestClient);

        String userJson2 = "{'firstName': 'Eve', 'lastName': 'ld', 'email': 'eve.ld@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user2 = Utils.pushUser(userJson2, webTestClient);

        String startDate1 = LocalDate.now().toString();
        String endDate1 = LocalDate.now().plusDays(3).toString();
        String vacationJson1 = ("{'email': 'del.vr@mail.com', 'startDate': '" + startDate1 + "' , 'endDate': '" + endDate1 + "'}")
            .replace('\'', '"');
        Utils.pushVacation(vacationJson1, webTestClient);    

        String startDate2 = LocalDate.now().plusDays(5).toString();
        String endDate2 = LocalDate.now().plusDays(10).toString();
        String vacationJson2 = ("{'email': 'eve.ld@mail.com', 'startDate': '" + startDate2 + "' , 'endDate': '" + endDate2 + "'}")
            .replace('\'', '"');
        Utils.pushVacation(vacationJson2, webTestClient);    

        webTestClient.get().uri("/api/users/vacations?email=del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].startDate").isEqualTo(startDate1)
            .jsonPath("$[0].endDate").isEqualTo(endDate1)
            .jsonPath("$[0].userId").isEqualTo(user1.getId());

        webTestClient.get().uri("/api/users/vacations?email=eve.ld@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].startDate").isEqualTo(startDate2)
            .jsonPath("$[0].endDate").isEqualTo(endDate2)
            .jsonPath("$[0].userId").isEqualTo(user2.getId());
    }

    /**
     * Test delte a vacation
     */

    @Test
    public void testDeleteVacation1() {
        String userJson = "{'firstName': 'Del', 'lastName': 'vr', 'email': 'del.vr@mail.com', 'professions': ['Danseur']}"
            .replace('\'', '"');
        User user = Utils.pushUser(userJson, webTestClient);

        String startDate1 = LocalDate.now().toString();
        String endDate1 = LocalDate.now().plusDays(3).toString();
        String vacationJson1 = ("{'email': 'del.vr@mail.com', 'startDate': '" + startDate1 + "' , 'endDate': '" + endDate1 + "'}")
            .replace('\'', '"');
        Utils.pushVacation(vacationJson1, webTestClient);    

        String startDate2 = LocalDate.now().plusDays(5).toString();
        String endDate2 = LocalDate.now().plusDays(10).toString();
        String vacationJson2 = ("{'email': 'del.vr@mail.com', 'startDate': '" + startDate2 + "' , 'endDate': '" + endDate2 + "'}")
            .replace('\'', '"');
        Utils.pushVacation(vacationJson2, webTestClient);  
        
        String deletevacationJson2 = ("{'userId':" + user.getId() + ", 'startDate': '" + startDate2 + "' , 'endDate': '" + endDate2 + "'}")
            .replace('\'', '"');
        webTestClient.method(HttpMethod.DELETE).uri("/api/vacations")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(deletevacationJson2)
            .exchange()
            .expectStatus().isNoContent();

        webTestClient.get().uri("/api/users/vacations?email=del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].startDate").isEqualTo(startDate1)
            .jsonPath("$[0].endDate").isEqualTo(endDate1)
            .jsonPath("$[0].userId").isEqualTo(user.getId());

        String deletevacationJson1 = ("{'userId':" + user.getId() + ", 'startDate': '" + startDate1 + "' , 'endDate': '" + endDate1 + "'}")
            .replace('\'', '"');
        webTestClient.method(HttpMethod.DELETE).uri("/api/vacations")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(deletevacationJson1)
            .exchange()
            .expectStatus().isNoContent();

        webTestClient.get().uri("/api/users/vacations?email=del.vr@mail.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(0);
    }

}
