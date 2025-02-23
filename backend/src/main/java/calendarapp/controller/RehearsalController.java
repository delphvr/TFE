package calendarapp.controller;

import calendarapp.model.Rehearsal;
import calendarapp.model.User;
import calendarapp.request.RehearsalRequest;
import calendarapp.response.RehearsalResponse;
import calendarapp.services.RehearsalService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/rehearsals/{id}")
    public ResponseEntity<Rehearsal> getRehearsal(@PathVariable("id") long id) {
        Rehearsal participants = rehearsalService.getRehearsal(id);
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }

    @PostMapping("/rehearsals")
    public ResponseEntity<Rehearsal> createRehearsal(@Valid @RequestBody RehearsalRequest request) {
        Rehearsal rehearsal = rehearsalService.createRehearsal(request);
        return new ResponseEntity<>(rehearsal, HttpStatus.CREATED);
    }

    @GetMapping("/rehearsals/{id}/participants")
    public ResponseEntity<List<User>> getRehearsalParticipants(@PathVariable("id") long id) {
        List<User> participants = rehearsalService.getRehearsalParticipants(id);
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }

    @DeleteMapping("/rehearsals/{id}")
    public ResponseEntity<HttpStatus> deleteRehearsal(@PathVariable("id") long id) {
        rehearsalService.deleteRehearsal(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/rehearsals/{id}")
    public ResponseEntity<Rehearsal> updateReheasal(@PathVariable("id") long id,
            @RequestBody RehearsalRequest rehearsal) {
        Rehearsal updatedRehearsal = rehearsalService.updateReheasal(id, rehearsal);
        return new ResponseEntity<>(updatedRehearsal, HttpStatus.OK);
    }

    @GetMapping("/users/{email}/projects/{projectId}/rehearsals")
    public ResponseEntity<List<RehearsalResponse>> getUserRehearsalsForProject(@PathVariable("email") String email,
            @PathVariable("projectId") long projectId) {
        List<RehearsalResponse> rehearsals = rehearsalService.getUserRehearsalsForProject(email, projectId);
        return new ResponseEntity<>(rehearsals, HttpStatus.OK);
    }

}
