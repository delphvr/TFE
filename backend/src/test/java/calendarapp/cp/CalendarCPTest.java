package calendarapp.cp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import calendarapp.Utils;
import calendarapp.model.CpResult;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("calendar_cp_test")
public class CalendarCPTest {
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
         *  project_id: 23, rehearsal_id 771, beginning_date 2025-04-01 10:00:00
         *  project_id: 23, rehearsal_id 772, beginning_date 2025-04-02
         *  project_id: 23, rehearsal_id 773, beginning_date 2025-04-03
         * 
         */       

        List<CpResult> results = Utils.getCpResults(23, webTestClient);

        assertEquals(3, results.size(), "There should be 3 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 771 || res.getRehearsalId() == 772 || res.getRehearsalId() == 773, "Wrong rehearsal id");
            assertEquals(23, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getProjectId());
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
         *  id 775, Rehearsal with only first participant, duration 3h, project_id: 24, participants: User1
         *  id 776, Rehearsal with only second participant, duration 3h, project_id: 24, participants: User2
         * 
         * Res:
         *  project_id: 24, rehearsal_id 774, beginning_date 2025-04-01 10:00:00
         *  project_id: 24, rehearsal_id 775, beginning_date 2025-04-02 
         *  project_id: 24, rehearsal_id 776, beginning_date 
         * 
         */       

        List<CpResult> results = Utils.getCpResults(24, webTestClient);

        assertEquals(3, results.size(), "There should be 3 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 774 || res.getRehearsalId() == 775 || res.getRehearsalId() == 776, "Wrong rehearsal id");
            assertEquals(24, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getProjectId());
            if(res.getRehearsalId() == 774){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 1, 10, 0, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 775){
                LocalDate expectedBeginningDate = LocalDate.of(2025, 4, 2);
                assertEquals(expectedBeginningDate, res.getBeginningDate().toLocalDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong date");
            }
        }
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
         *  project_id: 25, rehearsal_id 777, beginning_date 2025-04-13
         * 
         */       

        List<CpResult> results = Utils.getCpResults(25, webTestClient);

        assertEquals(1, results.size(), "There should be only 1 rehearsals");
        for(CpResult res: results){
            assertEquals(res.getRehearsalId(), 777, "Wrong rehearsal id");
            assertEquals(25, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getProjectId());
            LocalDate expectedBeginningDate = LocalDate.of(2025, 4, 13);
            assertEquals(expectedBeginningDate, res.getBeginningDate().toLocalDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong date");
        }
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
         *  project_id: 26, rehearsal_id 778, beginning_date 2025-04-07 10:00:00
         *  project_id: 26, rehearsal_id 779, beginning_date 2025-04-07 11:30:00
         * 
         */       

        List<CpResult> results = Utils.getCpResults(26, webTestClient);

        assertEquals(2, results.size(), "There should be 2 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 778 || res.getRehearsalId() == 779, "Wrong rehearsal id");
            assertEquals(26, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getProjectId());
            if(res.getRehearsalId() == 778){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 8, 10, 0, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 779){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 8, 11, 30, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }
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
         *  project_id: 27, rehearsal_id 1, beginning_date 2025-04-07 11:30:00
         *  project_id: 27, rehearsal_id 2, beginning_date 2025-04-07 10:00:00
         * 
         */       

        List<CpResult> results = Utils.getCpResults(27, webTestClient);

        assertEquals(2, results.size(), "There should be 2 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 1 || res.getRehearsalId() == 2, "Wrong rehearsal id");
            assertEquals(27, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getProjectId());
            if(res.getRehearsalId() == 1){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 8, 11, 30, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 2){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 8, 10, 0, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }
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
         *  project_id: 28, rehearsal_id 3, beginning_date 2025-04-02 10:00:00
         *  project_id: 28, rehearsal_id 4, beginning_date 2025-04-03 22:30:00
         * 
         */       

        List<CpResult> results = Utils.getCpResults(28, webTestClient);

        assertEquals(2, results.size(), "There should be 2 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 3 || res.getRehearsalId() == 4 , "Wrong rehearsal id");
            assertEquals(28, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertTrue(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getProjectId());
            if(res.getRehearsalId() == 3){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 2, 10, 00, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 4){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 3, 22, 30, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }
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
         *  project_id: 29, rehearsal_id 5, beginning_date 2025-04-07
         *  project_id: 29, rehearsal_id 6, beginning_date 2025-04-07
         * 
         */       

        List<CpResult> results = Utils.getCpResults(29, webTestClient);

        assertEquals(2, results.size(), "There should be 2 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 5 || res.getRehearsalId() == 6 , "Wrong rehearsal id");
            assertEquals(29, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getProjectId());
            if(res.getRehearsalId() == 5){
                LocalDate expectedBeginningDate = LocalDate.of(2025, 4, 7);
                assertEquals(expectedBeginningDate, res.getBeginningDate().toLocalDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong date");
            } else if(res.getRehearsalId() == 6){
                LocalDate expectedBeginningDate = LocalDate.of(2025, 4, 7);
                assertEquals(expectedBeginningDate, res.getBeginningDate().toLocalDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong date");
            }
        }
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
         *  project_id: 30, rehearsal_id 7, beginning_date 2025-04-08 10:00:00
         *  project_id: 30, rehearsal_id 8, beginning_date 2025-04-08 12:00:00
         * 
         */       

        List<CpResult> results = Utils.getCpResults(30, webTestClient);

        assertEquals(2, results.size(), "There should be 2 rehearsals");
        for(CpResult res: results){
            assertTrue(res.getRehearsalId() == 7 || res.getRehearsalId() == 8, "Wrong rehearsal id");
            assertEquals(30, res.getProjectId(), "Rehearsal " + res.getRehearsalId() + " has the wrong project id");
            assertFalse(res.isAccepted(), "The result should not be marked as accepted for rehearsal " + res.getProjectId());
            if(res.getRehearsalId() == 7){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 8, 10, 0, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            } else if(res.getRehearsalId() == 8){
                LocalDateTime expectedBeginningDateTime = LocalDateTime.of(2025, 4, 8, 12, 0, 0);
                assertEquals(expectedBeginningDateTime, res.getBeginningDate(), "Rehearsal " + res.getRehearsalId() + " has the wrong begining date time");
            }
        }
    }

}
