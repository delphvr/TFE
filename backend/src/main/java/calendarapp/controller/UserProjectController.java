package calendarapp.controller;

import calendarapp.model.Project;
import calendarapp.model.User;
import calendarapp.model.UserProject;
import calendarapp.request.CreateUserProjectRequest;
import calendarapp.response.UserProjectResponse;
import calendarapp.services.UserProjectService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@Validated
public class UserProjectController {

    @Autowired
    private UserProjectService userProjectService;

    @GetMapping("/userProjects")
    public ResponseEntity<List<UserProject>> getAllUserProjects() {
        try {
            List<UserProject> userProjects = userProjectService.getAllUserProjects();
            return new ResponseEntity<>(userProjects, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/userProjects")
    public ResponseEntity<UserProjectResponse> createUserProject(@Valid @RequestBody CreateUserProjectRequest request) {
        UserProjectResponse createdUserProjects = userProjectService.createUserProject(request);
        return new ResponseEntity<>(createdUserProjects, HttpStatus.CREATED);
    }

    @DeleteMapping("userProjects/{projectId}/users/{userId}")
    public ResponseEntity<HttpStatus> deleteUserProject(@PathVariable Long projectId, @PathVariable Long userId) {
        userProjectService.deleteUserProject(projectId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/userProjects")
    public ResponseEntity<HttpStatus> deleteAllUserProjects() {
        try {
            userProjectService.deleteAllUserProjects();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/userProjects/organizer/{email}")
    public ResponseEntity<List<Project>> getOrganizerProjects(@PathVariable("email") String email) {
        List<Project> organizerProject = userProjectService.getOrganizerProjects(email);
        return new ResponseEntity<>(organizerProject, HttpStatus.OK);
    }

    @GetMapping("/userProjects/{id}")
    public ResponseEntity<List<User>> getProjectUsers(@PathVariable("id") Long id) {
        List<User> organizerProject = userProjectService.getProjectUsers(id);
        return new ResponseEntity<>(organizerProject, HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}/users/{userId}/roles")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable Long projectId, @PathVariable Long userId) {
        List<String> roles = userProjectService.getUserRolesForProject(userId, projectId);
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
}
