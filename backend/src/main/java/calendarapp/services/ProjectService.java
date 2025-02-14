package calendarapp.services;

import calendarapp.model.Project;
import calendarapp.model.User;
import calendarapp.repository.ProjectRepository;
import calendarapp.repository.UserRepository;
import calendarapp.request.CreateProjectRequest;
import calendarapp.request.CreateUserProjectRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<Project>();
        projectRepository.findAll().forEach(projects::add);
        if (projects.isEmpty()) {
            throw new NoSuchElementException("No projects found in the database.");
        }
        return projects;
    }

    public List<Project> getProjectOfUser(String email){
        List<Project> projects = new ArrayList<Project>();
        List<Long> projectsId= userProjectService.getUserProjects(email);
        for (Long projectId : projectsId){
            Optional<Project> project = projectRepository.findById(projectId);
            project.ifPresent(projects::add);
        }
        return projects;
    }

    public Project createProject(CreateProjectRequest request) {
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
        CreateUserProjectRequest userPRojectRequest = new CreateUserProjectRequest(user.get().getEmail(), project.getId(), role);
        userProjectService.createUserProject(userPRojectRequest);
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

    public void deleteProject(long id) {
        projectRepository.deleteById(id);
    }

    public void deleteAllProjects() {
        projectRepository.deleteAll();
    }

}
