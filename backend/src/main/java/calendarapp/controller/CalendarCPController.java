package calendarapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import calendarapp.model.CpResult;
import calendarapp.services.CalendarCPService;
import calendarapp.services.CpResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Calendar CP", description = "APIs for the calendar CP computation")
public class CalendarCPController {
	@Autowired
	private CalendarCPService calendarCP;
	@Autowired
	private CpResultService cpResultService;

	@Operation(summary = "Run the cp and get the resulting calendar")
	@GetMapping("/projects/{id}/calendarCP")
	public ResponseEntity<List<CpResult>> runCalendarCP(@PathVariable("id") long id) {
		List<CpResult> res = calendarCP.run(id);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@Operation(summary = "Update the accepted state of a rehearsal in a project for the cp result")
	@PatchMapping("/projects/{projectId}/rehearsals/{rehearsalId}/accepted")
	public ResponseEntity<CpResult> updateRehearsalAcceptedState(@PathVariable("projectId") Long projectId,
			@PathVariable("rehearsalId") Long rehearsalId, @RequestParam("accepted") boolean accepted) {
		CpResult res = cpResultService.setIsAccepted(projectId, rehearsalId, accepted);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@Operation(summary = "Accept all CpResults for a project")
	@PutMapping("/projects/{projectId}/calendarCP/accept")
	public ResponseEntity<List<CpResult>> acceptAllCpResults(@PathVariable("projectId") Long projectId) {
		cpResultService.acceptAll(projectId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
