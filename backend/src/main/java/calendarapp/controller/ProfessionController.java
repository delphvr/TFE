package calendarapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import calendarapp.model.Profession;
import calendarapp.repository.ProfessionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
public class ProfessionController {
	@Autowired
	private ProfessionRepository professionRepository;

	@GetMapping("/professions")
	public ResponseEntity<List<Profession>> getAllProfessions() {
		List<Profession> professions = new ArrayList<Profession>();
		professionRepository.findAll().forEach(professions::add);
		if (professions.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(professions, HttpStatus.OK);
	}

	@PostMapping("/professions")
	public ResponseEntity<Profession> createProfession(@RequestBody Profession profession) {
		Profession _profession = professionRepository
				.save(new Profession(profession.getProfession()));
		return new ResponseEntity<>(_profession, HttpStatus.CREATED);
	}

	@DeleteMapping("/professions/{profession}")
	public ResponseEntity<HttpStatus> deleteProfession(@PathVariable("profession") String profession) {
		professionRepository.deleteById(profession);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
