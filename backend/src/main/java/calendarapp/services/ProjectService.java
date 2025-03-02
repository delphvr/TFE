package calendarapp.services;

import calendarapp.model.Project;
import calendarapp.model.User;
import calendarapp.repository.ProjectRepository;
import calendarapp.request.CreateProjectRequest;
import calendarapp.request.CreateUserProjectRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    /**
     * Checks if a project with the given ´projectId´ exists in the database.
     * If it does not exist, throws an IllegalArgumentException.
     * 
     * @param projectId: the id of a project
     * @throws IllegalArgumentException if no project is found with the given id
     */
    public void isProject(Long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (!project.isPresent()) {
            throw new IllegalArgumentException("Project not found with id " + projectId);
        }
    }

    /**
     * Get the project with id ´id´ from the database
     * 
     * @param id id of the project
     * @return the project with id ´id´
     * @throws IllegalArgumentException if no project is found with the given Id
     */
    public Project getProject(Long id) {
        Optional<Project> projectData = projectRepository.findById(id);
        if (!projectData.isPresent()) {
            throw new IllegalArgumentException("Project not found with id " + id);
        }
        return projectData.get();
    }

    /**
     * Get the list of projects of a user sorted by end date, then start date, then
     * by name.
     * 
     * @param email the email of a user
     * @return list of projects of the user
     * @throws IllegalArgumentException if no user is found with the given email
     */
    public List<Project> getProjectOfUser(String email) {
        userService.isUser(email);
        Set<Project> projects = new HashSet<>();
        List<Long> projectsId = userProjectService.getUserProjects(email);
        for (Long projectId : projectsId) {
            Optional<Project> project = projectRepository.findById(projectId);
            project.ifPresent(projects::add);
        }
        List<Project> res = new ArrayList<>(projects);
        res.sort(Comparator
                .comparing(Project::getEndingDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Project::getBeginningDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Project::getName, Comparator.naturalOrder()));
        return res;
    }

    /**
     * Create a project and saves it in the database, put the user with the given
     * email with the role organizer on the project.
     * 
     * @param request all the project informations (name, description, starting
     *                date, ending date, the organizer email;)
     * @return the newly created project
     * @throws IllegalArgumentException the ending date is in the past,
     *                                  or if no user is found with the given
     *                                  organizer email
     */
    public Project createProject(CreateProjectRequest request) {
        if (request.getEndingDate() != null) {
            LocalDate endingDate = request.getEndingDate();
            LocalDate now = LocalDate.now();
            if (endingDate.isBefore(now)) {
                throw new IllegalArgumentException("The ending date cannot be in the past");
            }
        }
        User user = userService.getUser(request.getOrganizerEmail());
        Project project = new Project(null, request.getName(), request.getDescription(), request.getBeginningDate(),
                request.getEndingDate());
        project = projectRepository.save(project);
        ArrayList<String> role = new ArrayList<>();
        role.add("Organizer");
        CreateUserProjectRequest userProjectRequest = new CreateUserProjectRequest(user.getEmail(),
                project.getId(), role);
        userProjectService.createUserProject(userProjectRequest);
        return project;
    }

    /**
     * Upadte the project with id ´id´ in the database with the updated iformation in ´project´.
     * 
     * @param id the id of the project
     * @param project the udated project informations
     * @return the updated project
     * @throws IllegalArgumentException if no project is found with the given id
     */
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

    /**
     * Delete the project with id ´id´ from the database
     * 
     * @param id the id of the project
     * @throws IllegalArgumentException if no project is found with the given id
     */
    public void deleteProject(Long id) {
        isProject(id);
        projectRepository.deleteById(id);
    }

}
