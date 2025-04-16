package calendarapp.cp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.hamcrest.Matchers.containsInAnyOrder;

import calendarapp.Utils;
import calendarapp.model.CpResult;
import calendarapp.model.RehearsalPresence;
import calendarapp.services.RehearsalService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("calendar_cp_test")
public class CalendarCPTest {
    @Autowired
    private RehearsalService rehearsalService;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testDisponibilitiesConstraints1() {         
        /**
         * User 1 (id 263) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 2(mercredi), start_time: 14h, end_time: 18h
         * User 2 (id 264) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 3(jeudi), start_time: 8h, end_time: 20h   
         * 
         * Project:
         *  id 23, beginning_date: 2025-03-30, ending_date: 2025-04-06
         * 
         * Rehearsals :
         *  id 771, Rehearsal with both participants, duration 3h, project_id: 23, participants: User1, User2
         *  id 772, Rehearsal with only first participant, duration 3h, project_id: 23, participants: User1
         *  id 773, Rehearsal with only second participant, duration 3h, project_id: 23, participants: User2
         * 
         * Res:
         *  project_id: 23, rehearsal_id 771, beginning_date 2025-04-01 10:00:00, User1 and User2 present
         *  project_id: 23, rehearsal_id 772, beginning_date 2025-04-02, User1 present
         *  project_id: 23, rehearsal_id 773, beginning_date 2025-04-03, User2 present
         * 
         */       

        List<CpResult> results = Utils.getCpResults(23, webTestClient, false);
        Map<Long, Map<Long, Boolean>> presences = Utils.getCpPresencesResults(23, webTestClient);

        assertEquals(3, results.size(), "There should be 3 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 771 || res.getRehearsalId() == 772 || res.getRehearsalId() == 773, "Wrong rehearsal id");
            assertEquals(23, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            if(res.getRehearsalId() == 771){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 1, 10, 0, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 772){
                LocalDate expectedBeginningDate = LocalDate.of(2025, 4, 2);
                assertEquals(expectedBeginningDate, res.getBeginningDate().toLocalDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong date");
            } else if(res.getRehearsalId() == 773){
                LocalDate expectedBeginningDate = LocalDate.of(2025, 4, 3);
                assertEquals(expectedBeginningDate, res.getBeginningDate().toLocalDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong date");
            }
        }
        assertTrue(presences.get(771L).get(263L), "User1 should be able to be present at the first rehearsal");
        assertTrue(presences.get(771L).get(264L), "User2 should be able to be present at the first rehearsal");
        assertTrue(presences.get(772L).get(263L), "User1 should be able to be present at the second rehearsal");
        assertTrue(presences.get(773L).get(264L), "User2 should be able to be present at the third rehearsal");

        webTestClient.get().uri("/api/rehearsals/771/CPpresences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.present.length()").isEqualTo(2)
            .jsonPath("$.present[*].id").value(containsInAnyOrder(263, 264))
            .jsonPath("$.notPresent.length()").isEqualTo(0);

        webTestClient.get().uri("/api/rehearsals/772/CPpresences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.present.length()").isEqualTo(1)
            .jsonPath("$.present[0].id").isEqualTo(263)
            .jsonPath("$.notPresent.length()").isEqualTo(0);
        
        webTestClient.get().uri("/api/rehearsals/773/CPpresences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.present.length()").isEqualTo(1)
            .jsonPath("$.present[0].id").isEqualTo(264)
            .jsonPath("$.notPresent.length()").isEqualTo(0);
    }

    @Test
    public void testDisponibilitiesConstraints2() {         
        /**
         * User 1 (id 263) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 2(mercredi), start_time: 14h, end_time: 18h
         * User 2 (id 264) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 3(jeudi), start_time: 8h, end_time: 20h   
         * 
         * Project:
         *  id 24, beginning_date: 2025-03-30, ending_date: 2025-04-02
         * 
         * Rehearsals :
         *  id 774, Rehearsal with both participants, duration 3h, project_id: 24, participants: User1, User2
         *  id 775, Rehearsal with only first participant, duration 4h, project_id: 24, participants: User1
         *  id 776, Rehearsal with only second participant, duration 4h, project_id: 24, participants: User2
         * 
         * Res:
         *  project_id: 24, rehearsal_id 774, beginning_date 2025-04-01 10:00:00, User1 and User2 present
         *  project_id: 24, rehearsal_id 775, beginning_date 2025-04-02 14:00:00, User1 present
         *  project_id: 24, rehearsal_id 776, beginning_date, User2 not present
         * 
         */       

        List<CpResult> results = Utils.getCpResults(24, webTestClient, false);
        Map<Long, Map<Long, Boolean>> presences = Utils.getCpPresencesResults(24, webTestClient);

        assertEquals(3, results.size(), "There should be 3 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 774 || res.getRehearsalId() == 775 || res.getRehearsalId() == 776, "Wrong rehearsal id");
            assertEquals(24, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            if(res.getRehearsalId() == 774){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 1, 10, 0, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 775){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 2, 14, 0, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong date");
            }
        }

        assertTrue(presences.get(774L).get(263L), "User1 should be able to be present at the first rehearsal");
        assertTrue(presences.get(774L).get(264L), "User2 should be able to be present at the first rehearsal");
        assertTrue(presences.get(775L).get(263L), "User1 should be able to be present at the second rehearsal");
        assertFalse(presences.get(776L).get(264L), "User2 should not be able to be present at the third rehearsal");

        webTestClient.get().uri("/api/rehearsals/774/CPpresences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.present.length()").isEqualTo(2)
            .jsonPath("$.present[*].id").value(containsInAnyOrder(263, 264))
            .jsonPath("$.notPresent.length()").isEqualTo(0);

        webTestClient.get().uri("/api/rehearsals/775/CPpresences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.present.length()").isEqualTo(1)
            .jsonPath("$.present[0].id").isEqualTo(263)
            .jsonPath("$.notPresent.length()").isEqualTo(0);
        
        webTestClient.get().uri("/api/rehearsals/776/CPpresences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.notPresent.length()").isEqualTo(1)
            .jsonPath("$.notPresent[0].id").isEqualTo(264)
            .jsonPath("$.present.length()").isEqualTo(0);
    }

    @Test
    public void testVacationConstraints3() {         
        /**
         * User 1 (id 265) disponibilities:
         *  weekday: 0(lundi), start_time: 7h, end_time: 23h
         *  weekday: 1(madri), start_time: 7h, end_time: 23h
         *  weekday: 2(mercredi), start_time: 7h, end_time: 23h
         *  weekday: 3(jeudi), start_time: 7h, end_time: 23h
         *  weekday: 4(vendredi), start_time: 7h, end_time: 23h
         *  weekday: 5(samedi), start_time: 7h, end_time: 23h
         *  weekday: 6(dimanche), start_time: 7h, end_time: 23h
         * User 1 (id 265) vacations:
         *  strat date: 2025-03-30, end_date: 2025-04-12
         *   
         * Project:
         *  id 25, beginning_date: 2025-03-30, ending_date: 2025-04-13
         * 
         * Rehearsals :
         *  id 777, Rehearsal with both participants, duration 2h, project_id: 25, participants: User1
         * 
         * Res:
         *  project_id: 25, rehearsal_id 777, beginning_date 2025-04-13, User1 present
         * 
         */       

        List<CpResult> results = Utils.getCpResults(25, webTestClient, false);
        Map<Long, Map<Long, Boolean>> presences = Utils.getCpPresencesResults(25, webTestClient);

        assertEquals(1, results.size(), "There should be only 1 rehearsals");
        for(CpResult res: results){
            assertEquals(res.getRehearsalId(), 777, "Wrong rehearsal id");
            assertEquals(25, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            LocalDate expectedBeginningDate = LocalDate.of(2025, 4, 13);
            assertEquals(expectedBeginningDate, res.getBeginningDate().toLocalDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong date");
        }

        assertTrue(presences.get(777L).get(265L), "User1 should be able to be present at the rehearsal");
    }

    @Test
    public void testPrecedenceConstraints1() {         
        /**
         * User 1 (id 263) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 2(mercredi), start_time: 14h, end_time: 18h
         * User 2 (id 264) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 3(jeudi), start_time: 8h, end_time: 20h   
         * 
         * Project:
         *  id 26, beginning_date: 2025-04-06, ending_date: 2025-04-08
         * 
         * Rehearsals :
         *  id 778, Rehearsal with only first participant, duration 1h30, project_id: 26, participants: User1
         *  id 779, Rehearsal with only second participant, duration 1h30, project_id: 26, participants: User2
         * 
         * Precedence Relation: 
         *  Rehearsal 778 before rehearsal 779
         * 
         * Res:
         *  project_id: 26, rehearsal_id 778, beginning_date 2025-04-07 10:00:00, User1 present
         *  project_id: 26, rehearsal_id 779, beginning_date 2025-04-07 11:30:00, User2 present
         * 
         */       

        List<CpResult> results = Utils.getCpResults(26, webTestClient, false);
        Map<Long, Map<Long, Boolean>> presences = Utils.getCpPresencesResults(26, webTestClient);

        assertEquals(2, results.size(), "There should be 2 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 778 || res.getRehearsalId() == 779, "Wrong rehearsal id");
            assertEquals(26, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            if(res.getRehearsalId() == 778){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 8, 10, 0, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 779){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 8, 11, 30, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }

        assertTrue(presences.get(778L).get(263L), "User1 should be able to be present at the first rehearsal");
        assertTrue(presences.get(779L).get(264L), "User2 should be able to be present at the second rehearsal");
    }

    @Test
    public void testPrecedenceConstraints2() {         
        /**
         * User 1 (id 263) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 2(mercredi), start_time: 14h, end_time: 18h
         * User 2 (id 264) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 3(jeudi), start_time: 8h, end_time: 20h   
         * 
         * Project:
         *  id 27, beginning_date: 2025-04-06, ending_date: 2025-04-08
         * 
         * Rehearsals :
         *  id 1, Rehearsal with only first participant, duration 1h30, project_id: 27, participants: User1
         *  id 2, Rehearsal with only second participant, duration 1h30, project_id: 27, participants: User2
         * 
         * Precedence Relation: 
         *  Rehearsal 2 before rehearsal 1
         * 
         * Res:
         *  project_id: 27, rehearsal_id 1, beginning_date 2025-04-07 11:30:00, User1 present
         *  project_id: 27, rehearsal_id 2, beginning_date 2025-04-07 10:00:00, User2 present
         * 
         */       

        List<CpResult> results = Utils.getCpResults(27, webTestClient, false);
        Map<Long, Map<Long, Boolean>> presences = Utils.getCpPresencesResults(27, webTestClient);

        assertEquals(2, results.size(), "There should be 2 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 1 || res.getRehearsalId() == 2, "Wrong rehearsal id");
            assertEquals(27, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            if(res.getRehearsalId() == 1){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 8, 11, 30, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 2){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 8, 10, 0, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }

        assertTrue(presences.get(1L).get(263L), "User1 should be able to be present at the first rehearsal");
        assertTrue(presences.get(2L).get(264L), "User2 should be able to be present at the second rehearsal");
    }

    @Test
    public void testSpecificDateTime() {         
        /**
         * User 1 (id 263) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 2(mercredi), start_time: 14h, end_time: 18h
         * User 2 (id 264) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 3(jeudi), start_time: 8h, end_time: 20h   
         * 
         * Project:
         *  id 28, beginning_date: 2025-04-01, ending_date: 2025-04-08
         * 
         * Rehearsals :
         *  id 3, date: 2025-04-02, time: 10:00:00, duration 2h, project_id: 28, participants: User1 and User2
         *  id 4, date: 2025-04-03, time: 22:30:00, duration 3h, project_id: 28, participants: User1 and User2
         * 
         * Res:
         *  project_id: 28, rehearsal_id 3, beginning_date 2025-04-02 10:00:00, User1 and User2 not present
         *  project_id: 28, rehearsal_id 4, beginning_date 2025-04-03 22:30:00, USer1 and User2 not present
         * 
         */       

        List<CpResult> results = Utils.getCpResults(28, webTestClient, false);
        Map<Long, Map<Long, Boolean>> presences = Utils.getCpPresencesResults(28, webTestClient);

        assertEquals(2, results.size(), "There should be 2 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 3 || res.getRehearsalId() == 4 , "Wrong rehearsal id");
            assertEquals(28, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertTrue(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            if(res.getRehearsalId() == 3){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 2, 10, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 4){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 3, 22, 30, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }
        assertFalse(presences.get(3L).get(263L), "User1 should not be able to be present at the first rehearsal");
        assertFalse(presences.get(3L).get(264L), "User2 should not be able to be present at the first rehearsal");
        assertFalse(presences.get(4L).get(263L), "User1 should not be able to be present at the second rehearsal");
        assertFalse(presences.get(4L).get(264L), "User2 should not be able to be present at the second rehearsal");
    }

    @Test
    public void testSpecificDate() {         
        /**
         * User 1 (id 263) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 2(mercredi), start_time: 14h, end_time: 18h
         * User 2 (id 264) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 3(jeudi), start_time: 8h, end_time: 20h   
         * 
         * Project:
         *  id 29, beginning_date: 2025-04-06, ending_date: 2025-04-28
         * 
         * Rehearsals :
         *  id 5, date: 2025-04-07, duration 2h, project_id: 29, participants: User1 and User2
         *  id 6, date: 2025-04-07, duration 3h, project_id: 29, participants: User1 and User2
         * 
         * Res:
         *  project_id: 29, rehearsal_id 5, beginning_date 2025-04-07, User1 and User2 not present
         *  project_id: 29, rehearsal_id 6, beginning_date 2025-04-07, User1 and User2 not present
         * 
         */       

        List<CpResult> results = Utils.getCpResults(29, webTestClient, false);
        Map<Long, Map<Long, Boolean>> presences = Utils.getCpPresencesResults(29, webTestClient);

        assertEquals(2, results.size(), "There should be 2 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 5 || res.getRehearsalId() == 6 , "Wrong rehearsal id");
            assertEquals(29, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            if(res.getRehearsalId() == 5){
                LocalDate expectedBeginningDate = LocalDate.of(2025, 4, 7);
                assertEquals(expectedBeginningDate, res.getBeginningDate().toLocalDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong date");
            } else if(res.getRehearsalId() == 6){
                LocalDate expectedBeginningDate = LocalDate.of(2025, 4, 7);
                assertEquals(expectedBeginningDate, res.getBeginningDate().toLocalDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong date");
            }
        }

        assertFalse(presences.get(5L).get(263L), "User1 should not be able to be present at the first rehearsal");
        assertFalse(presences.get(5L).get(264L), "User2 should not be able to be present at the first rehearsal");
        assertFalse(presences.get(6L).get(263L), "User1 should not be able to be present at the second rehearsal");
        assertFalse(presences.get(6L).get(264L), "User2 should not be able to be present at the second rehearsal");
    }

    @Test
    public void testSpecificTime() {         
        /**
         * User 1 (id 263) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 2(mercredi), start_time: 14h, end_time: 18h
         * User 2 (id 264) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 3(jeudi), start_time: 8h, end_time: 20h   
         * 
         * Project:
         *  id 30, beginning_date: 2025-04-06, ending_date: 2025-04-13
         * 
         * Rehearsals :
         *  id 7, time: 10:00:00, duration 2h, project_id: 30, participants: User1 and User2
         *  id 8, date: 12:00:00, duration 1h, project_id: 30, participants: User1 and User2
         * 
         * Res:
         *  project_id: 30, rehearsal_id 7, beginning_date 2025-04-08 10:00:00, User1 and User2 shoud be present
         *  project_id: 30, rehearsal_id 8, beginning_date 2025-04-08 12:00:00, User1 and User2 shoud be present
         * 
         */       

        List<CpResult> results = Utils.getCpResults(30, webTestClient, false);
        Map<Long, Map<Long, Boolean>> presences = Utils.getCpPresencesResults(30, webTestClient);

        assertEquals(2, results.size(), "There should be 2 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 7 || res.getRehearsalId() == 8, "Wrong rehearsal id");
            assertEquals(30, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            if(res.getRehearsalId() == 7){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 8, 10, 0, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 8){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 8, 12, 0, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }
        assertTrue(presences.get(7L).get(263L), "User1 should be able to be present at the first rehearsal");
        assertTrue(presences.get(7L).get(264L), "User2 should be able to be present at the first rehearsal");
        assertTrue(presences.get(8L).get(263L), "User1 should be able to be present at the second rehearsal");
        assertTrue(presences.get(8L).get(264L), "User2 should be able to be present at the second rehearsal");
    }

    @Test
    public void testRecomputation() {         
        /**
         * User 1 (id 263) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 2(mercredi), start_time: 14h, end_time: 18h
         * User 2 (id 264) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 3(jeudi), start_time: 8h, end_time: 20h   
         * 
         * Project:
         *  id 23, beginning_date: 2025-03-30, ending_date: 2025-04-06
         * 
         * Rehearsals :
         *  id 771, Rehearsal with both participants, duration 3h, project_id: 23, participants: User1, User2
         *  id 772, Rehearsal with only first participant, duration 3h, project_id: 23, participants: User1
         *  id 773, Rehearsal with only second participant, duration 3h, project_id: 23, participants: User2
         * 
         * 
         * Recompute with 771 and 773 accepted and 772 not accepted
         * 
         * Res:
         *  project_id: 23, rehearsal_id 771, beginning_date 2025-04-01 10:00:00, User1 and User2 present
         *  project_id: 23, rehearsal_id 772, beginning_date NOT 2025-04-02, User1 not present or is not present at the first one
         *  project_id: 23, rehearsal_id 773, beginning_date 2025-04-03, User2 present
         * 
         */       

        Utils.getCpResults(23, webTestClient, false);

        webTestClient.patch().uri("/api/projects/23/rehearsals/771/accepted?accepted=true")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.projectId").isEqualTo(23)
            .jsonPath("$.rehearsalId").isEqualTo(771)
            .jsonPath("$.accepted").isEqualTo(true);

        webTestClient.patch().uri("/api/projects/23/rehearsals/773/accepted?accepted=true")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.projectId").isEqualTo(23)
            .jsonPath("$.rehearsalId").isEqualTo(773)
            .jsonPath("$.accepted").isEqualTo(true);

        List<CpResult> results = Utils.getCpResults(23, webTestClient, true);

        Map<Long, Map<Long, Boolean>> presences = Utils.getCpPresencesResults(23, webTestClient);

        assertEquals(3, results.size(), "There should be 3 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 771 || res.getRehearsalId() == 772 || res.getRehearsalId() == 773, "Wrong rehearsal id");
            assertEquals(23, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            if(res.getRehearsalId() == 771){
                assertTrue(res.isAccepted(), "The result should be marked as accepted for rehearsal " + res.getRehearsalId());
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 1, 10, 0, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 772){
                assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getProjectId());
                LocalDate expectedBeginningDate = LocalDate.of(2025, 4, 2);
                assertFalse(expectedBeginningDate.equals(res.getBeginningDate().toLocalDate()), "Rehearsal " + res.getRehearsalId() + " has the wrong date");
            } else if(res.getRehearsalId() == 773){
                assertTrue(res.isAccepted(), "The result should be marked as accepted for rehearsal " + res.getProjectId());
                LocalDate expectedBeginningDate = LocalDate.of(2025, 4, 3);
                assertEquals(expectedBeginningDate, res.getBeginningDate().toLocalDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong date");
            }
        }
        assertTrue((presences.get(771L).get(263L) || presences.get(772L).get(263L)) && !((presences.get(771L).get(263L) && presences.get(772L).get(263L)))  , "User1 should be able to be present at both rehearsals");
        assertTrue(presences.get(771L).get(264L), "User2 should be able to be present at the first rehearsal");
        assertTrue(presences.get(773L).get(264L), "User2 should be able to be present at the third rehearsal");

        rehearsalService.updateReheasalDateAndTime(771L, 23L, null);
        rehearsalService.updateReheasalDateAndTime(773L, 23L, null);
    }

    @Test
    public void testAccepteAll() {       
        /**
         * User 1 (id 263) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 2(mercredi), start_time: 14h, end_time: 18h
         * User 2 (id 264) disponibilities:
         *  weekday: 1(mardi), start_time: 10h, end_time: 13h
         *  weekday: 3(jeudi), start_time: 8h, end_time: 20h   
         * 
         * Project:
         *  id 24, beginning_date: 2025-03-30, ending_date: 2025-04-02
         * 
         * Rehearsals :
         *  id 774, Rehearsal with both participants, duration 3h, project_id: 24, participants: User1, User2
         *  id 775, Rehearsal with only first participant, duration 4h, project_id: 24, participants: User1
         *  id 776, Rehearsal with only second participant, duration 4h, project_id: 24, participants: User2
         * 
         * Res:
         *  project_id: 24, rehearsal_id 774, beginning_date 2025-04-01 10:00:00, User1 and User2 present
         *  project_id: 24, rehearsal_id 775, beginning_date 2025-04-02 14:00:00, User1 present
         *  project_id: 24, rehearsal_id 776, beginning_date, User2 not present
         * 
         */       

        Utils.getCpResults(24, webTestClient, false);

        webTestClient.put().uri("/api/projects/24/calendarCP/accept")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[0].projectId").isEqualTo(24)
            .jsonPath("$[1].projectId").isEqualTo(24)
            .jsonPath("$[2].projectId").isEqualTo(24)
            .jsonPath("$[*].rehearsalId").value(containsInAnyOrder(774, 775, 776))
            .jsonPath("$[0].accepted").isEqualTo(true)
            .jsonPath("$[1].accepted").isEqualTo(true)
            .jsonPath("$[2].accepted").isEqualTo(true);

        webTestClient.get().uri("/api/rehearsals/774/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.notPresent.length()").isEqualTo(0)
            .jsonPath("$.present.length()").isEqualTo(2)
            .jsonPath("$.present[*].id").value(containsInAnyOrder(263, 264));

        webTestClient.get().uri("/api/rehearsals/775/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.present.length()").isEqualTo(1)
            .jsonPath("$.present[0].id").isEqualTo(263)
            .jsonPath("$.notPresent.length()").isEqualTo(0);

        webTestClient.get().uri("/api/rehearsals/776/presences")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.notPresent.length()").isEqualTo(1)
            .jsonPath("$.notPresent[0].id").isEqualTo(264)
            .jsonPath("$.present.length()").isEqualTo(0);

        rehearsalService.updateReheasalDateAndTime(774L, 24L, null);
        rehearsalService.updateReheasalDateAndTime(775L, 24L, null);
        rehearsalService.updateReheasalDateAndTime(776L, 24L, null);
    }

    @Test
    public void testConstraintsOnOrtherProjectRehearsals1() {       
        /**
         * User 1 (id 266) disponibilities:
         *  weekday: 0(lundi), start_time: 9h, end_time: 10h
         *  weekday: 1(mardi), start_time: 14h, end_time: 19h
         * User 2 (id 267) disponibilities:
         *  weekday: 1(mardi), start_time: 14h, end_time: 19h
         *  weekday: 3(jeudi), start_time: 10h, end_time: 12h30   
         * 
         * Project:
         *  id 31, beginning_date: 2025-04-21, ending_date: 2025-04-27
         *  id 33, beginning_date: 2025-04-28, ending_date: 2025-05-04
         * 
         * Rehearsals :
         *  id 780, 2h rehearsal with both participants, project_id: 31, participants: User1, User2
         *  id 781, 3h rehearsal with both participants, project_id: 31, participants: User1, User2
         *  id 782, 1h rehearsal with user 266 only, project_id: 31, participants: User1
         *  id 783, 2h30 rehearsal with user 267 only, project_id: 31, participants: User2
         *  id 788, 2h rehearsal with both participants, project_id: 33, participants: User1, User2
         *  id 789, 3h rehearsal with both participants, project_id: 33, participants: User1, User2
         *  id 790, 1h rehearsal with user 266 only, project_id: 33, participants: User1
         *  id 791, 2h30 rehearsal with user 267 only, project_id: 33, participants: User2
         * 
         * Res:
         *  project_id: 31, rehearsal_id 780, beginning_date 2025-04-22 14:00:00 || 17:00:00, User1 and User2 present
         *  project_id: 31, rehearsal_id 781, beginning_date 2025-04-22 14:00:00 || 16:00:00, User1 and User2 present
         *  project_id: 31, rehearsal_id 782, beginning_date 2025-04-21 09:00:00, User1 present
         *  project_id: 31, rehearsal_id 783, beginning_date 2025-04-24 10:00:00, User2 present
         *  project_id: 33, rehearsal_id 788, beginning_date 2025-04-29 14:00:00 || 17:00:00, User1 and User2 present
         *  project_id: 33, rehearsal_id 789, beginning_date 2025-04-29 14:00:00 || 16:00:00, User1 and User2 present
         *  project_id: 33, rehearsal_id 790, beginning_date 2025-04-28 09:00:00, User1 present
         *  project_id: 33, rehearsal_id 791, beginning_date 2025-05-01 10:00:00, User2 present
         * 
         */       

        List<CpResult> results = Utils.getCpResults(31, webTestClient, false);
        Map<Long, Map<Long, Boolean>> presences = Utils.getCpPresencesResults(31, webTestClient);

        assertEquals(4, results.size(), "There should be 4 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 780 || res.getRehearsalId() == 781 || res.getRehearsalId() == 782 || res.getRehearsalId() == 783, "Wrong rehearsal id");
            assertEquals(31, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            if(res.getRehearsalId() == 780){
                LocalDateTime expectedBeginningDateTime1 = LocalDateTime.of(2025, 4, 22, 14, 00, 0);
                LocalDateTime expectedBeginningDateTime2 = LocalDateTime.of(2025, 4, 22, 17, 00, 0);
                assertTrue(expectedBeginningDateTime1.equals(res.getBeginningDate()) || expectedBeginningDateTime2.equals(res.getBeginningDate()), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 781){
                LocalDateTime expectedBeginningDateTime1 = LocalDateTime.of(2025, 4, 22, 14, 00, 0);
                LocalDateTime expectedBeginningDateTime2 = LocalDateTime.of(2025, 4, 22, 16, 00, 0);
                assertTrue(expectedBeginningDateTime1.equals(res.getBeginningDate()) || expectedBeginningDateTime2.equals(res.getBeginningDate()), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 782){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 21, 9, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
            else if(res.getRehearsalId() == 783){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 24, 10, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }

        assertTrue(presences.get(780L).get(266L), "User1 should be able to be present at the first rehearsal");
        assertTrue(presences.get(781L).get(266L), "User1 should be able to be present at the second rehearsal");
        assertTrue(presences.get(780L).get(267L), "User2 should be able to be present at the first rehearsal");
        assertTrue(presences.get(781L).get(267L), "User2 should be able to be present at the second rehearsal");
        assertTrue(presences.get(782L).get(266L), "User1 should be able to be present at the third rehearsal");
        assertTrue(presences.get(783L).get(267L), "User2 should be able to be present at the fourth rehearsal"); 
        
        webTestClient.put().uri("/api/projects/31/calendarCP/accept")
            .exchange();

        results = Utils.getCpResults(33, webTestClient, false);
        presences = Utils.getCpPresencesResults(33, webTestClient);

        assertEquals(4, results.size(), "There should be 4 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 788 || res.getRehearsalId() == 789 || res.getRehearsalId() == 790 || res.getRehearsalId() == 791, "Wrong rehearsal id");
            assertEquals(33, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            if(res.getRehearsalId() == 788){
                LocalDateTime expectedBeginningDateTime1 = LocalDateTime.of(2025, 4, 29, 14, 00, 0);
                LocalDateTime expectedBeginningDateTime2 = LocalDateTime.of(2025, 4, 29, 17, 00, 0);
                assertTrue(expectedBeginningDateTime1.equals(res.getBeginningDate()) || expectedBeginningDateTime2.equals(res.getBeginningDate()), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 789){
                LocalDateTime expectedBeginningDateTime1 = LocalDateTime.of(2025, 4, 29, 14, 00, 0);
                LocalDateTime expectedBeginningDateTime2 = LocalDateTime.of(2025, 4, 29, 16, 00, 0);
                assertTrue(expectedBeginningDateTime1.equals(res.getBeginningDate()) || expectedBeginningDateTime2.equals(res.getBeginningDate()), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 790){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 28, 9, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
            else if(res.getRehearsalId() == 791){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 5, 1, 10, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }

        assertTrue(presences.get(788L).get(266L), "User1 should be able to be present at the first rehearsal");
        assertTrue(presences.get(789L).get(266L), "User1 should be able to be present at the second rehearsal");
        assertTrue(presences.get(788L).get(267L), "User2 should be able to be present at the first rehearsal");
        assertTrue(presences.get(789L).get(267L), "User2 should be able to be present at the second rehearsal");
        assertTrue(presences.get(790L).get(266L), "User1 should be able to be present at the third rehearsal");
        assertTrue(presences.get(791L).get(267L), "User2 should be able to be present at the fourth rehearsal");
        
        rehearsalService.updateReheasalDateAndTime(780L, 31L, null);
        rehearsalService.updateReheasalDateAndTime(781L, 31L, null);
        rehearsalService.updateReheasalDateAndTime(782L, 31L, null);
        rehearsalService.updateReheasalDateAndTime(783L, 31L, null);
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(780L, 266L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(780L, 267L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(781L, 266L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(781L, 267L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(782L, 266L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(783L, 267L, false));
    }

    @Test
    public void testConstraintsOnOrtherProjectRehearsals2() {       
        /**
         * User 1 (id 266) disponibilities:
         *  weekday: 0(lundi), start_time: 9h, end_time: 10h
         *  weekday: 1(mardi), start_time: 14h, end_time: 19h
         * User 2 (id 267) disponibilities:
         *  weekday: 1(mardi), start_time: 14h, end_time: 19h
         *  weekday: 3(jeudi), start_time: 10h, end_time: 12h30   
         * 
         * Project:
         *  id 32, beginning_date: 2025-04-28, ending_date: 2025-05-04
         *  id 33, beginning_date: 2025-04-28, ending_date: 2025-05-04
         * 
         * Rehearsals :
         *  id 784, 2h rehearsal with both participants, project_id: 32, participants: User1, User2
         *  id 785, 3h rehearsal with both participants, project_id: 32, participants: User1, User2
         *  id 786, 1h rehearsal with user 266 only, project_id: 32, participants: User1
         *  id 787, 2h30 rehearsal with user 267 only, project_id: 32, participants: User2
         *  id 788, 2h rehearsal with both participants, project_id: 33, participants: User1, User2
         *  id 789, 3h rehearsal with both participants, project_id: 33, participants: User1, User2
         *  id 790, 1h rehearsal with user 266 only, project_id: 33, participants: User1
         *  id 791, 2h30 rehearsal with user 267 only, project_id: 33, participants: User2
         * 
         * Res:
         *  project_id: 33, rehearsal_id 788, beginning_date 2025-04-29 14:00:00 || 17:00:00, User1 and User2 present
         *  project_id: 33, rehearsal_id 789, beginning_date 2025-04-29 14:00:00 || 16:00:00, User1 and User2 present
         *  project_id: 33, rehearsal_id 790, beginning_date 2025-04-28 09:00:00, User1 present
         *  project_id: 33, rehearsal_id 791, beginning_date 2025-05-01 10:00:00, User2 present
         *  project_id: 32, rehearsal_id 784, beginning_date 2025-04-22 14:00:00 || 17:00:00, User1 and User2 present
         *  project_id: 32, rehearsal_id 785, beginning_date 2025-04-22 14:00:00 || 16:00:00, User1 and User2 present
         *  project_id: 32, rehearsal_id 786, beginning_date 2025-04-21 09:00:00, User1 present
         *  project_id: 32, rehearsal_id 787, beginning_date 2025-04-24 10:00:00, User2 present
         * 
         */       

        List<CpResult> results = Utils.getCpResults(33, webTestClient, false);
        Map<Long, Map<Long, Boolean>> presences = Utils.getCpPresencesResults(33, webTestClient);

        assertEquals(4, results.size(), "There should be 4 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 788 || res.getRehearsalId() == 789 || res.getRehearsalId() == 790 || res.getRehearsalId() == 791, "Wrong rehearsal id");
            assertEquals(33, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            if(res.getRehearsalId() == 788){
                LocalDateTime expectedBeginningDateTime1 = LocalDateTime.of(2025, 4, 29, 14, 00, 0);
                LocalDateTime expectedBeginningDateTime2 = LocalDateTime.of(2025, 4, 29, 17, 00, 0);
                assertTrue(expectedBeginningDateTime1.equals(res.getBeginningDate()) || expectedBeginningDateTime2.equals(res.getBeginningDate()), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 789){
                LocalDateTime expectedBeginningDateTime1 = LocalDateTime.of(2025, 4, 29, 14, 00, 0);
                LocalDateTime expectedBeginningDateTime2 = LocalDateTime.of(2025, 4, 29, 16, 00, 0);
                assertTrue(expectedBeginningDateTime1.equals(res.getBeginningDate()) || expectedBeginningDateTime2.equals(res.getBeginningDate()), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 790){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 28, 9, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
            else if(res.getRehearsalId() == 791){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 5, 1, 10, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }

        assertTrue(presences.get(788L).get(266L), "User1 should be able to be present at the first rehearsal");
        assertTrue(presences.get(789L).get(266L), "User1 should be able to be present at the second rehearsal");
        assertTrue(presences.get(788L).get(267L), "User2 should be able to be present at the first rehearsal");
        assertTrue(presences.get(789L).get(267L), "User2 should be able to be present at the second rehearsal");
        assertTrue(presences.get(790L).get(266L), "User1 should be able to be present at the third rehearsal");
        assertTrue(presences.get(791L).get(267L), "User2 should be able to be present at the fourth rehearsal");
        
        webTestClient.put().uri("/api/projects/33/calendarCP/accept")
            .exchange();

        results = Utils.getCpResults(32, webTestClient, false);
        presences = Utils.getCpPresencesResults(32, webTestClient);

        assertEquals(4, results.size(), "There should be 4 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 784 || res.getRehearsalId() == 785 || res.getRehearsalId() == 786 || res.getRehearsalId() == 787, "Wrong rehearsal id");
            assertEquals(32, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            if(res.getRehearsalId() == 784){
                LocalDateTime expectedBeginningDateTime1 = LocalDateTime.of(2025, 4, 22, 14, 00, 0);
                LocalDateTime expectedBeginningDateTime2 = LocalDateTime.of(2025, 4, 22, 17, 00, 0);
                assertTrue(expectedBeginningDateTime1.equals(res.getBeginningDate()) || expectedBeginningDateTime2.equals(res.getBeginningDate()), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 785){
                LocalDateTime expectedBeginningDateTime1 = LocalDateTime.of(2025, 4, 22, 14, 00, 0);
                LocalDateTime expectedBeginningDateTime2 = LocalDateTime.of(2025, 4, 22, 16, 00, 0);
                assertTrue(expectedBeginningDateTime1.equals(res.getBeginningDate()) || expectedBeginningDateTime2.equals(res.getBeginningDate()), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 786){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 21, 9, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
            else if(res.getRehearsalId() == 787){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 24, 10, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }

        assertTrue(presences.get(784L).get(266L), "User1 should be able to be present at the first rehearsal");
        assertTrue(presences.get(785L).get(266L), "User1 should be able to be present at the second rehearsal");
        assertTrue(presences.get(784L).get(267L), "User2 should be able to be present at the first rehearsal");
        assertTrue(presences.get(785L).get(267L), "User2 should be able to be present at the second rehearsal");
        assertTrue(presences.get(786L).get(266L), "User1 should be able to be present at the third rehearsal");
        assertTrue(presences.get(787L).get(267L), "User2 should be able to be present at the fourth rehearsal"); 
        
        rehearsalService.updateReheasalDateAndTime(788L, 33L, null);
        rehearsalService.updateReheasalDateAndTime(789L, 33L, null);
        rehearsalService.updateReheasalDateAndTime(790L, 33L, null);
        rehearsalService.updateReheasalDateAndTime(791L, 33L, null);
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(788L, 266L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(788L, 267L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(789L, 266L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(789L, 267L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(790L, 266L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(791L, 267L, false));
        
    }

    @Test
    public void testConstraintsOnOrtherProjectRehearsals3() {       
        /**
         * User 1 (id 266) disponibilities:
         *  weekday: 0(lundi), start_time: 9h, end_time: 10h
         *  weekday: 1(mardi), start_time: 14h, end_time: 19h
         * User 2 (id 267) disponibilities:
         *  weekday: 1(mardi), start_time: 14h, end_time: 19h
         *  weekday: 3(jeudi), start_time: 10h, end_time: 12h30   
         * 
         * Project:
         *  id 31, beginning_date: 2025-04-21, ending_date: 2025-04-27
         *  id 32, beginning_date: 2025-04-28, ending_date: 2025-05-04
         * 
         * Rehearsals :
         *  id 780, 2h rehearsal with both participants, project_id: 31, participants: User1, User2
         *  id 781, 3h rehearsal with both participants, project_id: 31, participants: User1, User2
         *  id 782, 1h rehearsal with user 266 only, project_id: 31, participants: User1
         *  id 783, 2h30 rehearsal with user 267 only, project_id: 31, participants: User2
         *  id 784, 2h rehearsal with both participants, project_id: 32, participants: User1, User2
         *  id 785, 3h rehearsal with both participants, project_id: 32, participants: User1, User2
         *  id 786, 1h rehearsal with user 266 only, project_id: 32, participants: User1
         *  id 787, 2h30 rehearsal with user 267 only, project_id: 32, participants: User2
         * 
         * Res:
         *  project_id: 31, rehearsal_id 780, beginning_date 2025-04-22 14:00:00 || 17:00:00, User1 and User2 present
         *  project_id: 31, rehearsal_id 781, beginning_date 2025-04-22 14:00:00 || 16:00:00, User1 and User2 present
         *  project_id: 31, rehearsal_id 782, beginning_date 2025-04-21 09:00:00, User1 present
         *  project_id: 31, rehearsal_id 782, beginning_date 2025-04-24 10:00:00, User2 present
         *  project_id: 32, rehearsal_id 788, beginning_date 2025-04-29 14:00:00 || 17:00:00, User1 and User2 present
         *  project_id: 32, rehearsal_id 789, beginning_date 2025-04-29 14:00:00 || 16:00:00, User1 and User2 present
         *  project_id: 32, rehearsal_id 790, beginning_date 2025-04-28 09:00:00, User1 present
         *  project_id: 32, rehearsal_id 791, beginning_date 2025-05-01 10:00:00, User2 present
         * 
         */  

        List<CpResult> results = Utils.getCpResults(31, webTestClient, false);
        Map<Long, Map<Long, Boolean>> presences = Utils.getCpPresencesResults(31, webTestClient);

        assertEquals(4, results.size(), "There should be 4 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 780 || res.getRehearsalId() == 781 || res.getRehearsalId() == 782 || res.getRehearsalId() == 783, "Wrong rehearsal id");
            assertEquals(31, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            if(res.getRehearsalId() == 780){
                LocalDateTime expectedBeginningDateTime1 = LocalDateTime.of(2025, 4, 22, 14, 00, 0);
                LocalDateTime expectedBeginningDateTime2 = LocalDateTime.of(2025, 4, 22, 17, 00, 0);
                assertTrue(expectedBeginningDateTime1.equals(res.getBeginningDate()) || expectedBeginningDateTime2.equals(res.getBeginningDate()), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 781){
                LocalDateTime expectedBeginningDateTime1 = LocalDateTime.of(2025, 4, 22, 14, 00, 0);
                LocalDateTime expectedBeginningDateTime2 = LocalDateTime.of(2025, 4, 22, 16, 00, 0);
                assertTrue(expectedBeginningDateTime1.equals(res.getBeginningDate()) || expectedBeginningDateTime2.equals(res.getBeginningDate()), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 782){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 21, 9, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
            else if(res.getRehearsalId() == 783){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 24, 10, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }

        assertTrue(presences.get(780L).get(266L), "User1 should be able to be present at the first rehearsal");
        assertTrue(presences.get(781L).get(266L), "User1 should be able to be present at the second rehearsal");
        assertTrue(presences.get(780L).get(267L), "User2 should be able to be present at the first rehearsal");
        assertTrue(presences.get(781L).get(267L), "User2 should be able to be present at the second rehearsal");
        assertTrue(presences.get(782L).get(266L), "User1 should be able to be present at the third rehearsal");
        assertTrue(presences.get(783L).get(267L), "User2 should be able to be present at the fourth rehearsal"); 
        
        webTestClient.put().uri("/api/projects/31/calendarCP/accept")
            .exchange();

        results = Utils.getCpResults(32, webTestClient, false);
        presences = Utils.getCpPresencesResults(32, webTestClient);

        assertEquals(4, results.size(), "There should be 4 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 784 || res.getRehearsalId() == 785 || res.getRehearsalId() == 786 || res.getRehearsalId() == 787, "Wrong rehearsal id");
            assertEquals(32, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getRehearsalId());
            if(res.getRehearsalId() == 784){
                LocalDateTime expectedBeginningDateTime1 = LocalDateTime.of(2025, 4, 29, 14, 00, 0);
                LocalDateTime expectedBeginningDateTime2 = LocalDateTime.of(2025, 4, 29, 17, 00, 0);
                assertTrue(expectedBeginningDateTime1.equals(res.getBeginningDate()) || expectedBeginningDateTime2.equals(res.getBeginningDate()), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 785){
                LocalDateTime expectedBeginningDateTime1 = LocalDateTime.of(2025, 4, 29, 14, 00, 0);
                LocalDateTime expectedBeginningDateTime2 = LocalDateTime.of(2025, 4, 29, 16, 00, 0);
                assertTrue(expectedBeginningDateTime1.equals(res.getBeginningDate()) || expectedBeginningDateTime2.equals(res.getBeginningDate()), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 786){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 28, 9, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
            else if(res.getRehearsalId() == 787){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 5, 1, 10, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }

        assertTrue(presences.get(784L).get(266L), "User1 should be able to be present at the first rehearsal");
        assertTrue(presences.get(785L).get(266L), "User1 should be able to be present at the second rehearsal");
        assertTrue(presences.get(784L).get(267L), "User2 should be able to be present at the first rehearsal");
        assertTrue(presences.get(785L).get(267L), "User2 should be able to be present at the second rehearsal");
        assertTrue(presences.get(786L).get(266L), "User1 should be able to be present at the third rehearsal");
        assertTrue(presences.get(787L).get(267L), "User2 should be able to be present at the fourth rehearsal"); 
        
        rehearsalService.updateReheasalDateAndTime(780L, 31L, null);
        rehearsalService.updateReheasalDateAndTime(781L, 31L, null);
        rehearsalService.updateReheasalDateAndTime(782L, 31L, null);
        rehearsalService.updateReheasalDateAndTime(783L, 31L, null);
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(780L, 266L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(780L, 267L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(781L, 266L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(781L, 267L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(782L, 266L, false));
        rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(783L, 267L, false));
    }

}
