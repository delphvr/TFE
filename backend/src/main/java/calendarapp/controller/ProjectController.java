package calendarapp.controller;

import calendarapp.model.Project;
import calendarapp.request.CreateProjectRequest;
import calendarapp.services.ProjectService;
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

@RestController
@RequestMapping("/api")
@Tag(name = "Project", description = "APIs for managing projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Operation(summary = "Get a project informations")
    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable("id") Long id) {
        Project project = projectService.getProject(id);
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @Operation(summary = "Get the projects of a user")
    @GetMapping("/projects/user/{email}")
    public ResponseEntity<List<Project>> getProjectByEmail(@PathVariable("email") String email) {
        List<Project> projects = projectService.getProjectOfUser(email);
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @Operation(summary = "Create a project")
    @PostMapping("/projects")
    public ResponseEntity<Project> createProject(@Valid @RequestBody CreateProjectRequest request) {
        Project createdProject = projectService.createProject(request);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a project")
    @PutMapping("/projects/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable("id") long id, @RequestBody Project project) {
        Project updatedProject = projectService.updateProject(id, project);
        return new ResponseEntity<>(updatedProject, HttpStatus.OK);
    }

    @Operation(summary = "Delete a project")
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<HttpStatus> deleteProject(@PathVariable("id") Long id) {
        projectService.deleteProject(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
