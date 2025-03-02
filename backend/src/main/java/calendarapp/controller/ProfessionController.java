package calendarapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import calendarapp.model.Profession;
import calendarapp.services.ProfessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@Tag(name = "Profession", description = "APIs for managing the possible professions")
public class ProfessionController {
	@Autowired
	private ProfessionService professionService;

	@Operation(summary = "Get the list of possible professions")
	@GetMapping("/professions")
	public ResponseEntity<List<Profession>> getAllProfessions() {
		List<Profession> professions = professionService.getAllProfessions();
		return new ResponseEntity<>(professions, HttpStatus.OK);
	}

}
