package calendarapp.cp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("calendar_cp_test")
public class CalendarCPTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void test1() {         
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

        webTestClient.get().uri("/api/projects/23/calendarCP")
            .exchange();
    }

    @Test
    public void test2() {         
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
         *  TODO
         * 
         */       

        webTestClient.get().uri("/api/projects/24/calendarCP")
            .exchange();
    }

}
