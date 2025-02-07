package calendarapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import calendarapp.model.Profession;
import calendarapp.repository.ProfessionRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test") 
public class ProfessionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProfessionRepository professionRepository;

    @BeforeEach
    public void cleanUpDatabase() {
        professionRepository.deleteAll();
    }

    /*
     * Tests get all professions
     */

    @Test
    public void testGetProfessions() {
        professionRepository.save(new Profession("Chorégraphe"));
        professionRepository.save(new Profession("Danseuse"));
        professionRepository.save(new Profession("Directeur artistique"));

        webTestClient.get().uri("/api/professions")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .json("[{\"profession\":\"Chorégraphe\"}, {\"profession\":\"Danseuse\"}, {\"profession\":\"Directeur artistique\"}]");
    }

    @Test
    public void testGetProfessionsZero() {
        webTestClient.get().uri("/api/professions")
            .exchange()
            .expectStatus().isNoContent();
    }
    
}
