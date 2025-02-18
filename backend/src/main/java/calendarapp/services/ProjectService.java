package calendarapp.services;

import calendarapp.model.Project;
import calendarapp.model.User;
import calendarapp.repository.ProjectRepository;
import calendarapp.repository.UserRepository;
import calendarapp.request.CreateProjectRequest;
import calendarapp.request.CreateUserProjectRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserProjectService userProjectService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Checks if a project with the given ´projectId´ exists in the database.
     * If it does not exist, throws an IllegalArgumentException.
     * @param projectId: the id of a project 
     * @throws IllegalArgumentException if no project is found with the given ID
     */
    public void isProject(Long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (!project.isPresent()) {
            throw new IllegalArgumentException("Project not found with id " + projectId);
        }
    }

    public List<Project> getProjectOfUser(String email) {
        List<Project> projects = new ArrayList<Project>();
        List<Long> projectsId = userProjectService.getUserProjects(email);
        for (Long projectId : projectsId) {
            Optional<Project> project = projectRepository.findById(projectId);
            project.ifPresent(projects::add);
        }
        return projects;
    }

    public Project createProject(CreateProjectRequest request) {
        if (request.getEndingDate() != null) {
            LocalDate endingDate = request.getEndingDate(); 
            LocalDate now = LocalDate.now(); 
            if (endingDate.isBefore(now)) {
                throw new IllegalArgumentException("The ending date cannot be in the past");
            } 
        }
        Optional<User> user = userRepository.findByEmail(request.getOrganizerEmail());
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with email " + request.getOrganizerEmail());
        }
        if (!userService.isUserOrganizer(request.getOrganizerEmail())) {
            throw new IllegalArgumentException("User needs to be an organizer to create a project");
        }
        Project project = new Project(null, request.getName(), request.getDescription(), request.getBeginningDate(),
                request.getEndingDate());
        project = projectRepository.save(project);
        ArrayList<String> role = new ArrayList<>();
        role.add("Organizer");
        CreateUserProjectRequest userProjectRequest = new CreateUserProjectRequest(user.get().getEmail(),
                project.getId(), role);
        userProjectService.createUserProject(userProjectRequest);
        return project;
    }

    public Project updateProject(long id, Project project) {
        Optional<Project> projectData = projectRepository.findById(id);
        if (projectData.isPresent()) {
            Project _project = projectData.get();
            _project.setName(project.getName());
            _project.setDescription(project.getDescription());
            _project.setBeginningDate(project.getBeginningDate());
            _project.setEndingDate(project.getEndingDate());
            return projectRepository.save(_project);
        } else {
            throw new IllegalArgumentException("Project not found with id " + id);
        }
    }

    public void deleteAllProjects() {
        projectRepository.deleteAll();
    }

    public void deleteProject(Long id) {
        Optional<Project> project = projectRepository.findById(id);
        if (!project.isPresent()) {
            throw new IllegalArgumentException("Project not found with id " + id);
        }
        projectRepository.deleteById(id);
    }

}
