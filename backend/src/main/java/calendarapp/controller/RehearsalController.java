package calendarapp.controller;

import calendarapp.model.Rehearsal;
import calendarapp.model.RehearsalPresence;
import calendarapp.model.User;
import calendarapp.request.RehearsalRequest;
import calendarapp.response.RehearsalPrecedenceResponse;
import calendarapp.response.RehearsalResponse;
import calendarapp.services.RehearsalPrecedenceService;
import calendarapp.services.RehearsalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
@Tag(name = "Rehearsal", description = "APIs for managing rehearsals")
public class RehearsalController {

    @Autowired
    private RehearsalService rehearsalService;
    @Autowired
    private RehearsalPrecedenceService rehearsalPrecedenceService;

    @Operation(summary = "Get the rehearsals of a project")
    @GetMapping("/projects/{id}/rehearsals")
    public ResponseEntity<List<RehearsalResponse>> getProjectRehearsals(@PathVariable("id") long id) {
        List<RehearsalResponse> rehearsals = rehearsalService.getProjectRehearsals(id);
        return new ResponseEntity<>(rehearsals, HttpStatus.OK);
    }

    @Operation(summary = "Get a rehearsal")
    @GetMapping("/rehearsals/{id}")
    public ResponseEntity<Rehearsal> getRehearsal(@PathVariable("id") long id) {
        Rehearsal participants = rehearsalService.getRehearsal(id);
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }

    @Operation(summary = "Create a rehearsal")
    @PostMapping("/rehearsals")
    public ResponseEntity<Rehearsal> createRehearsal(@Valid @RequestBody RehearsalRequest request) {
        Rehearsal rehearsal = rehearsalService.createRehearsal(request);
        return new ResponseEntity<>(rehearsal, HttpStatus.CREATED);
    }

    @Operation(summary = "Get the rehearsals of a project")
    @GetMapping("/rehearsals/{id}/participants")
    public ResponseEntity<List<User>> getRehearsalParticipants(@PathVariable("id") long id) {
        List<User> participants = rehearsalService.getRehearsalParticipants(id);
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }

    @Operation(summary = "Delete a rehearsal")
    @DeleteMapping("/rehearsals/{id}")
    public ResponseEntity<HttpStatus> deleteRehearsal(@PathVariable("id") long id) {
        rehearsalService.deleteRehearsal(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Update a rehearsal")
    @PutMapping("/rehearsals/{id}")
    public ResponseEntity<Rehearsal> updateReheasal(@PathVariable("id") long id,
            @RequestBody RehearsalRequest rehearsal) {
        Rehearsal updatedRehearsal = rehearsalService.updateReheasal(id, rehearsal);
        return new ResponseEntity<>(updatedRehearsal, HttpStatus.OK);
    }

    @Operation(summary = "Get the rehearsals of a user on a project")
    @GetMapping("/users/{email}/projects/{projectId}/rehearsals")
    public ResponseEntity<List<RehearsalResponse>> getUserRehearsalsForProject(@PathVariable("email") String email,
            @PathVariable("projectId") long projectId) {
        List<RehearsalResponse> rehearsals = rehearsalService.getUserRehearsalsForProject(email, projectId);
        return new ResponseEntity<>(rehearsals, HttpStatus.OK);
    }

    @Operation(summary = "Get all the rehearsals of a user")
    @GetMapping("/users/{email}/rehearsals")
    public ResponseEntity<List<Rehearsal>> getUserRehearsals(@PathVariable("email") String email) {
        List<Rehearsal> rehearsals = rehearsalService.getUserRehearsals(email);
        return new ResponseEntity<>(rehearsals, HttpStatus.OK);
    }

    @Operation(summary = "Update a rehearsal, rehearsals precedence relations")
    @PostMapping("/rehearsals/{id}/precedences")
    public ResponseEntity<HttpStatus> addRehearsalPrecedence(@PathVariable("id") long rehearsalId,
            @RequestBody List<Long> precedingRehearsalsId) {
        rehearsalPrecedenceService.createRehearsalPrecedences(rehearsalId, precedingRehearsalsId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get a rehearsal precedence relations")
    @GetMapping("/rehearsals/{id}/precedences")
    public ResponseEntity<RehearsalPrecedenceResponse> getRehearsalPrecedences(@PathVariable("id") long rehearsalId) {
        RehearsalPrecedenceResponse res = rehearsalPrecedenceService.getRehersalsPrecedences(rehearsalId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Delete a rehearsal precedence relations")
    @DeleteMapping("/rehearsals/precedences")
    public ResponseEntity<HttpStatus> deleteRehearsalPrecedence(
            @RequestParam Long current,
            @RequestParam Long previous) {
        rehearsalPrecedenceService.deleteRehearsalPrecedence(current, previous);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get the users presence for the rehearsals of the project")
	@GetMapping("/projects/{id}/presences")
	public ResponseEntity<Map<Long, Map<Long, Boolean>>> getRehearsalsPresences(@PathVariable("id") long id) {
		Map<Long, Map<Long, Boolean>> res = rehearsalService.getPresences(id);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

    @Operation(summary = "Update the user presence for the rehearsal")
    @PutMapping("/rehearsals/{rehearsalId}/users/{userId}/presences")
    public ResponseEntity<RehearsalPresence> updateUserPresence(@PathVariable("rehearsalId") long rehearsalId,
            @PathVariable("userId") long userId, @RequestParam Boolean presence) { 
        RehearsalPresence res = rehearsalService.createOrUpdateRehearsalPresence(new RehearsalPresence(rehearsalId, userId, presence)); 
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
