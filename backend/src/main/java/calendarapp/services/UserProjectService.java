package calendarapp.services;

import calendarapp.model.Organizer;
import calendarapp.model.Project;
import calendarapp.model.Role;
import calendarapp.model.User;
import calendarapp.model.UserProject;
import calendarapp.model.UserProjectId;
import calendarapp.repository.OrganizerRepository;
import calendarapp.repository.ProjectRepository;
import calendarapp.repository.RoleRepository;
import calendarapp.repository.UserProjectRepository;
import calendarapp.repository.UserRepository;
import calendarapp.request.CreateUserProjectRequest;
import calendarapp.response.UserProjectResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserProjectService {

    @Autowired
    private UserProjectRepository userProjectRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private OrganizerRepository organizerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<UserProject> getAllUserProjects() {
        List<UserProject> userProjects = new ArrayList<UserProject>();
        userProjectRepository.findAll().forEach(userProjects::add);
        if (userProjects.isEmpty()) {
            throw new NoSuchElementException("No users project relations found in the database.");
        }
        return userProjects;
    }

    public UserProjectResponse createUserProject(CreateUserProjectRequest request) {
        List<String> roles = new ArrayList<>();
        Optional<User> user = userRepository.findByEmail(request.getUserEmail());
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with email " + request.getUserEmail());
        }
        Long userId = user.get().getId();

        Optional<Project> project = projectRepository.findById(request.getProjectId());
        if (!project.isPresent()) {
            throw new IllegalArgumentException("Project not found with ID " + request.getProjectId());
        }

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String role : request.getRoles()) {
                Optional<Role> existingRole = roleRepository.findById(role);
                if (!existingRole.isPresent()) {
                    Role r = new Role(role);
                    roleRepository.save(r);
                }
                if ("Organizer".equals(role)) {
                    Organizer organizer = new Organizer(userId);
                    organizerRepository.save(organizer);
                }
                UserProject userProject = new UserProject(userId, request.getProjectId(), role);
                userProjectRepository.save(userProject);
                roles.add(role);
            }
        }

        return new UserProjectResponse(userId, request.getProjectId(), roles);
    }

    public void deleteUserProject(UserProject request) {
        UserProjectId id = new UserProjectId(request.getUserId(), request.getProjectId(), request.getRole());
        userProjectRepository.deleteById(id);
    }

    public void deleteAllUserProjects() {
        userProjectRepository.deleteAll();
    }

    public List<Long> getOrganizerProjects(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with email " + email);
        }
        Long userId = user.get().getId();
        List<Long> res = new ArrayList<>();
        List<UserProject> userProjects = userProjectRepository.findByUserIdAndRole(userId, "Organizer");
        if (userProjects.isEmpty()) {
            return res;
        }
        for (UserProject userProject : userProjects) {
            res.add(userProject.getProjectId());
        }
        return res;
    }

    public List<Long> getUserProjects(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with email " + email);
        }
        Long userId = user.get().getId();
        List<Long> res = new ArrayList<>();
        List<UserProject> userProjects = userProjectRepository.findByUserId(userId);
        if (userProjects.isEmpty()) {
            return res;
        }
        for (UserProject userProject : userProjects) {
            res.add(userProject.getProjectId());
        }
        return res;
    }
}
