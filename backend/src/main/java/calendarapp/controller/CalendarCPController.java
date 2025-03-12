package calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import calendarapp.services.CalendarCPService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Calendar CP", description = "APIs for the calendar CP computation")
public class CalendarCPController {
    @Autowired
	private CalendarCPService calendarCP;

	@Operation(summary = "Run the cp and get the resulting calendar")
	@GetMapping("/projects/{id}/calendarCP")
	public ResponseEntity<String> runCalendarCP(@PathVariable("id") long id) {
		String res = calendarCP.run(id);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}    
}
