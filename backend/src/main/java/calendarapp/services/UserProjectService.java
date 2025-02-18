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
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

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
        } else {
            Optional<Role> existingRole = roleRepository.findById("Non défini");
            if (!existingRole.isPresent()) {
                Role r = new Role("Non défini");
                roleRepository.save(r);
            }
            UserProject userProject = new UserProject(userId, request.getProjectId(), "Non défini");
            userProjectRepository.save(userProject);
            roles.add("Non défini");
        }

        return new UserProjectResponse(userId, request.getProjectId(), roles);
    }

    public void deleteAllUserProjects() {
        userProjectRepository.deleteAll();
    }

    public List<Project> getOrganizerProjects(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with email " + email);
        }
        Long userId = user.get().getId();
        Set<Project> projects = new HashSet<>();
        List<UserProject> userProjects = userProjectRepository.findByUserIdAndRole(userId, "Organizer");
        if (userProjects.isEmpty()) {
            return new ArrayList<>(projects);
        }
        for (UserProject userProject : userProjects) {
            Optional<Project> p = projectRepository.findById(userProject.getProjectId());
            projects.add(p.get());
        }
        List<Project> res = new ArrayList<>(projects);
        // Sort based on endingDate, then beginningDate
        res.sort(Comparator
                .comparing(Project::getEndingDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Project::getBeginningDate, Comparator.nullsLast(Comparator.naturalOrder())));

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

    public List<User> getProjectUsers(Long id) {
        Set<User> users = new HashSet<>();
        Optional<Project> projcet = projectRepository.findById(id);
        if (!projcet.isPresent()) {
            throw new IllegalArgumentException("Project not found with id " + id);
        }
        List<UserProject> userProjects = userProjectRepository.findByProjectId(id);
        if (userProjects.isEmpty()) {
            return new ArrayList<>(users);
        }
        for (UserProject userProject : userProjects) {
            Optional<User> user = userRepository.findById(userProject.getUserId());
            users.add(user.get());
        }
        // Sort based on last name, then first name
        List<User> res = new ArrayList<>(users);
        res.sort(Comparator.comparing(User::getLastName, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(User::getFirstName, Comparator.nullsLast(Comparator.naturalOrder())));
        return res;
    }

    @Transactional
    public void deleteUserProject(Long projectId, Long userId) {
        Optional<Project> projcet = projectRepository.findById(projectId);
        if (!projcet.isPresent()) {
            throw new IllegalArgumentException("Project not found with id " + projectId);
        }
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with id " + userId);
        }
        userProjectRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    public List<String> getUserRolesForProject(Long userId, Long projectId) {
        Optional<Project> projcet = projectRepository.findById(projectId);
        if (!projcet.isPresent()) {
            throw new IllegalArgumentException("Project not found with id " + projectId);
        }
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with id " + userId);
        }
        List<String> res = new ArrayList<>();
        List<UserProject> userProjects = userProjectRepository.findByUserIdAndProjectId(userId, projectId);
        for (UserProject userProject : userProjects) {
            res.add(userProject.getRole());
        }
        return res;
    }

    @Transactional
    public void deleteUserRole(Long projectId, Long userId, String role) {
        Optional<Project> projcet = projectRepository.findById(projectId);
        if (!projcet.isPresent()) {
            throw new IllegalArgumentException("Project not found with id " + projectId);
        }
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with id " + userId);
        }
        List<UserProject> userProjects = userProjectRepository.findByUserIdAndProjectId(userId, projectId);
        boolean roleFound = false;
        for (UserProject userProject : userProjects) {
            if (userProject.getRole().equals(role)) {
                roleFound = true;
                userProjectRepository.deleteByProjectIdAndUserIdAndRole(projectId, userId, role);
            }
        }
        if (!roleFound) {
            throw new IllegalArgumentException("Role " + role + " not found.");
        }
        if (userProjects.size() == 1 && roleFound) {
            Optional<Role> existingRole = roleRepository.findById("Non défini");
            if (!existingRole.isPresent()) {
                Role r = new Role("Non défini");
                roleRepository.save(r);
            }
            UserProject userProject = new UserProject(userId, projectId, "Non défini");
            userProjectRepository.save(userProject);
        }
    }

    @Transactional
    public UserProjectResponse addUserRolesToUserInProject(Long userId, Long projectId, List<String> roles) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with id " + userId);
        }
        Optional<Project> project = projectRepository.findById(projectId);
        if (!project.isPresent()) {
            throw new IllegalArgumentException("Project not found with id " + projectId);
        }
        List<String> addedRoles = new ArrayList<>();
        for (String role : roles) {
            Optional<Role> existingRole = roleRepository.findById(role);
            if (!existingRole.isPresent()) {
                Role newRole = new Role(role);
                roleRepository.save(newRole);
            }
            Optional<UserProject> existingUserProject = userProjectRepository
                    .findById(new UserProjectId(userId, projectId, role));
            if (!existingUserProject.isPresent()) {
                if ("Organizer".equals(role)) {
                    Organizer organizer = new Organizer(userId);
                    organizerRepository.save(organizer);
                }
                UserProject userProject = new UserProject(userId, projectId, role);
                userProjectRepository.save(userProject);
                addedRoles.add(role);
            }
        }
        if (addedRoles.size() > 0 && !addedRoles.contains("Non défini")) {
            Optional<UserProject> userProject = userProjectRepository
                    .findById(new UserProjectId(userId, projectId, "Non défini"));
            if (userProject.isPresent()) {
                UserProjectId id = new UserProjectId(userId, projectId, "Non défini");
                userProjectRepository.deleteById(id);
            }
        }
        return new UserProjectResponse(userId, projectId, addedRoles);
    }

}
