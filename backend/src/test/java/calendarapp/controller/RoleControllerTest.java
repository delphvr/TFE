package calendarapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import calendarapp.model.Role;
import calendarapp.repository.RoleRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test") 
public class RoleControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void cleanUpDatabase() {
        //roleRepository.deleteAll();
    }

    /*
     * Tests get all roles
     */

    @Test
    public void testGetRoles() {
        roleRepository.save(new Role("Chorégraphe"));
        roleRepository.save(new Role("Danseuse"));
        roleRepository.save(new Role("Directeur artistique"));

        webTestClient.get().uri("/api/roles")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[?(@.role == 'Chorégraphe')]").exists()
            .jsonPath("$[?(@.role == 'Danseuse')]").exists()
            .jsonPath("$[?(@.role == 'Directeur artistique')]").exists();
    }

    /*@Test
    public void testGetProfessionsZero() {
        webTestClient.get().uri("/api/roles")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .json("[]");
    }*/
    
}
