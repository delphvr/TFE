package calendarapp.controller;

import calendarapp.model.User;
import calendarapp.request.UserRequest;
import calendarapp.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//source: https://www.bezkoder.com/spring-boot-postgresql-example/

@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "APIs for managing users")
public class UserController {

	@Autowired
	private UserService userService;

	@Operation(summary = "Get a user")
	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
		User user = userService.getUser(id);
		return ResponseEntity.ok(user);
	}

	@Operation(summary = "Get a user by email")
	@GetMapping("/users")
	public ResponseEntity<User> getUserByEmail(@RequestParam("email") String email) {
		User user = userService.getUser(email);
		return ResponseEntity.ok(user);
	}

	@Operation(summary = "Create a user")
	@PostMapping("/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest request) {
		User createdUser = userService.createUser(request);
		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}

	@Operation(summary = "Update a user")
	@PutMapping("/users/{id}")
	public ResponseEntity<User> updateUser(@PathVariable("id") long id, @RequestBody UserRequest user) {
		User updatedUser = userService.updateUser(id, user);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	@Operation(summary = "Delete a user")
	@DeleteMapping("/users/{email}")
	public ResponseEntity<HttpStatus> deleteUser(@PathVariable("email") String email) {
		userService.deleteByEmail(email);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Operation(summary = "Get a user professions")
	@GetMapping("/users/{email}/professions")
	public ResponseEntity<List<String>> getUserProfessions(@PathVariable("email") String email) {
		List<String> professions = userService.getUserProfessions(email);
		return new ResponseEntity<>(professions, HttpStatus.OK);
	}
}
