package calendarapp.controller;

import calendarapp.model.Project;
import calendarapp.model.User;
import calendarapp.request.CreateUserProjectRequest;
import calendarapp.request.RolesRequest;
import calendarapp.response.UserProjectResponse;
import calendarapp.services.UserProjectService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
@Validated
public class UserProjectController {

    @Autowired
    private UserProjectService userProjectService;

    @PostMapping("/userProjects")
    public ResponseEntity<UserProjectResponse> createUserProject(@Valid @RequestBody CreateUserProjectRequest request) {
        UserProjectResponse createdUserProjects = userProjectService.createUserProject(request);
        return new ResponseEntity<>(createdUserProjects, HttpStatus.CREATED);
    }

    @PostMapping("/projects/{projectId}/users/{userId}/roles")
    public ResponseEntity<UserProjectResponse> addUserRoles(@PathVariable Long projectId, @PathVariable Long userId,
            @RequestBody RolesRequest roles) {
        UserProjectResponse response = userProjectService.addUserRolesToUserInProject(userId, projectId,
                roles.getRoles());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/projects/{projectId}/users/{userId}/roles")
    public ResponseEntity<UserProjectResponse> updateUserRoles(@PathVariable Long projectId, @PathVariable Long userId,
            @RequestBody RolesRequest roles) {
        UserProjectResponse response = userProjectService.updateUserRolesToUserInProject(userId, projectId,
                roles.getRoles());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("projects/{projectId}/users/{userId}")
    public ResponseEntity<HttpStatus> deleteUserProject(@PathVariable Long projectId, @PathVariable Long userId) {
        userProjectService.deleteUserProject(projectId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("projects/{projectId}/users")
    public ResponseEntity<HttpStatus> deleteUserProject(@PathVariable Long projectId, @RequestParam String email) {
        userProjectService.deleteUserProject(projectId, email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    @GetMapping("/projects/{projectId}/users/roles")
    public ResponseEntity<List<String>> getUserRolesByEmail(@PathVariable Long projectId, @RequestParam String email) {
        List<String> roles = userProjectService.getUserRolesForProjectByEmail(email, projectId);
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @DeleteMapping("/projects/{projectId}/users/{userId}/roles/{role}")
    public ResponseEntity<HttpStatus> deleteUserRole(@PathVariable Long projectId, @PathVariable Long userId,
            @PathVariable String role) {
        userProjectService.deleteUserRole(projectId, userId, role);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/projects/{projectId}/is-organizer")
    public ResponseEntity<Map<String, Boolean>> isUserOrganizer(
            @PathVariable Long projectId,
            @RequestParam String email) {
        Map<String, Boolean> isOrganizer = userProjectService.isUserOrganizer(email, projectId);
        return new ResponseEntity<>(isOrganizer, HttpStatus.OK);
    }
}
