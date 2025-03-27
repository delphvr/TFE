package calendarapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import calendarapp.model.Vacation;
import calendarapp.services.VacationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Vacation", description = "APIs for dealing with the vacations")
public class VacationController {

    @Autowired
    private VacationService vacationService;

    @Operation(summary = "Get a user vacations")
    @GetMapping("/users/vacations")
    public ResponseEntity<List<Vacation>> getUserVacations(@RequestParam String email) {
        List<Vacation> availabilities = vacationService.getUserVacations(email);
        return new ResponseEntity<>(availabilities, HttpStatus.OK);
    }

    @Operation(summary = "Add a vacations to the database")
    @GetMapping("/vacations")
    public ResponseEntity<Vacation> createVacation(@RequestBody Vacation vacation) {
        Vacation res = vacationService.createVacation(vacation);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a vacation")
	@DeleteMapping("/vacations")
    public ResponseEntity<HttpStatus> deleteWeeklyAvailability(@RequestBody Vacation vacation) {
        vacationService.deleteVacation(vacation);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
