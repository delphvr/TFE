package calendarapp.controller;

import calendarapp.model.Rehearsal;
import calendarapp.request.CreateRehearsalRequest;
import calendarapp.response.RehearsalResponse;
import calendarapp.services.RehearsalService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
public class RehearsalController {

    @Autowired
    private RehearsalService rehearsalService;

    @GetMapping("/projects/{id}/rehearsals")
    public ResponseEntity<List<RehearsalResponse>> getProjectRehearsals(@PathVariable("id") long id) {
        List<RehearsalResponse> rehearsals = rehearsalService.getProjectRehearsals(id);
        return new ResponseEntity<>(rehearsals, HttpStatus.OK);
    }

    @PostMapping("/rehearsals")
    public ResponseEntity<Rehearsal> createRehearsal(@Valid @RequestBody CreateRehearsalRequest request) {
        Rehearsal rehearsal = rehearsalService.createRehearsal(request);
        return new ResponseEntity<>(rehearsal, HttpStatus.CREATED);
    }

    //TODO get list of user object for a rehearsal

    //TODO delete une crépétition

    //TODO update le projet

}
