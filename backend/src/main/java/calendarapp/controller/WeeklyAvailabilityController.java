package calendarapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import calendarapp.model.WeeklyAvailability;
import calendarapp.request.CreateWeeklyAvailabilityRequest;
import calendarapp.services.WeeklyAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Weekly availabilities", description = "APIs for weekly availabilities of users")
public class WeeklyAvailabilityController {

    @Autowired
	private WeeklyAvailabilityService weeklyAvailabilityService;

    @Operation(summary = "Get a user weekly availabilities")
	@GetMapping("/users/availabilities")
    public ResponseEntity<List<WeeklyAvailability>> getUserAvailabilities(@RequestParam String email) {
        List<WeeklyAvailability> availabilities = weeklyAvailabilityService.getUserAvailabilities(email);
		return new ResponseEntity<>(availabilities, HttpStatus.OK);
    }
    
    @Operation(summary = "Add a weekly availabilities to the database")
	@PostMapping("/availabilities")
    public ResponseEntity<List<WeeklyAvailability>> createWeeklyAvailability(@RequestBody CreateWeeklyAvailabilityRequest weeklyAvailabilities) {
        List<WeeklyAvailability> availabilities = weeklyAvailabilityService.createAvailability(weeklyAvailabilities);
		return new ResponseEntity<>(availabilities, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a weekly availability from the database")
	@DeleteMapping("/availabilities")
    public ResponseEntity<HttpStatus> deleteWeeklyAvailability(@RequestBody WeeklyAvailability weeklyAvailability) {
        weeklyAvailabilityService.deleteWeeklyAvailability(weeklyAvailability);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
