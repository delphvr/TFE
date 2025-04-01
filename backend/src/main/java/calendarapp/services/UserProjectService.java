package calendarapp.services;

import calendarapp.model.Project;
import calendarapp.model.Role;
import calendarapp.model.User;
import calendarapp.model.UserProject;
import calendarapp.model.UserProjectId;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserProjectService {

    @Autowired
    private UserProjectRepository userProjectRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserService userService;

    /**
     * Checks if a project with the given ´projectId´ exists in the database.
     * If it does not exist, throws an IllegalArgumentException.
     * 
     * @param projectId: the id of a project
     * @throws IllegalArgumentException if no project is found with the given Id
     */
    public void isProject(Long projectId) {
        if(projectId == null){
            throw new IllegalArgumentException("Project not found with id " + projectId);
        }
        Optional<Project> project = projectRepository.findById(projectId);
        if (!project.isPresent()) {
            throw new IllegalArgumentException("Project not found with id " + projectId);
        }
    }

    /**
     * Checks if a role exists in the database.
     * If it does not exist, throws an IllegalArgumentException.
     * 
     * @param role: the role
     * @throws IllegalArgumentException if the role is not found
     */
    public void isRole(String roleStr) {
        if(roleStr == null){
            throw new IllegalArgumentException("Role " + roleStr + " not found.");
        }
        Optional<Role> role = roleRepository.findById(roleStr);
        if (!role.isPresent()) {
            throw new IllegalArgumentException("Role " + roleStr + " not found.");
        }
    }

    /**
     * Add a user to a project with a list of roles. Saves it in the database. If
     * the user doesn't have a role on the project yet he will get the role 'Non
     * défini'. If the user had the role 'Non défini' and now has new roles then the
     * role 'Non défini' will be removed.
     * 
     * @param request the user email, the project id and the list of roles
     * @return the newly add User Project
     * @throws IllegalArgumentException if no user is found with the given email,
     *                                  or if no project is found with the given Id,
     *                                  or if one of the roles doesn't exist
     */
    public UserProjectResponse createUserProject(CreateUserProjectRequest request) {
        List<String> roles = new ArrayList<>();
        User user = userService.getUser(request.getUserEmail());
        Long userId = user.getId();
        isProject(request.getProjectId());

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String role : request.getRoles()) {
                isRole(role);
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

    /**
     * Get the list of project for wich the user is an organizer. The list of
     * project will be sorted by ending date, then starting date, then by name.
     * 
     * @param email the email of the user
     * @return the list of project where the user is an organizer.
     * @throws IllegalArgumentException if no user is found with the given email
     */
    public List<Project> getOrganizerProjects(String email) {
        User user = userService.getUser(email);
        Long userId = user.getId();
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
        // Sort based on endingDate, then beginningDate, the project name
        res.sort(Comparator
                .comparing(Project::getEndingDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Project::getBeginningDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Project::getName, Comparator.naturalOrder()));

        return res;
    }

    /**
     * Get the list of projects id of the user.
     * 
     * @param email the email of the user
     * @return a list of project id, for which the user is a part of
     * @throws IllegalArgumentException if no user is found with the given email
     */
    public List<Long> getUserProjects(String email) {
        User user = userService.getUser(email);
        Long userId = user.getId();
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

    /**
     * Get the list of user that take part in the project with id ´id´. The list is
     * sorted by last name then by first name.
     * 
     * @param id the id of the project
     * @return the list of user participating in the projcet.
     * @throws IllegalArgumentException if no project is found with the given id
     */
    public List<User> getProjectUsers(Long id) {
        Set<User> users = new HashSet<>();
        isProject(id);
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

    /**
     * Delete a user from a project
     * 
     * @param projectId the id of the project
     * @param userId    the id of the user
     * @throws IllegalArgumentException if no user is found with the given id,
     *                                  or no project found with the given id,
     *                                  or the user is the only organizer on the
     *                                  project
     */
    @Transactional
    public void deleteUserProject(Long projectId, Long userId) {
        userService.isUser(userId);
        isProject(projectId);
        List<UserProject> userProjects = userProjectRepository.findByUserIdAndProjectIdAndRole(userId, projectId,
                "Organizer");
        if (!userProjects.isEmpty()) { // the user is an organizer
            List<UserProject> organizers = userProjectRepository.findByProjectIdAndRole(projectId, "Organizer");
            if (organizers.size() == 1) {
                throw new IllegalArgumentException("Au moins un organisateur dois être présent sur le projet");
            }
        }
        userProjectRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    /**
     * Delete the user with the given email from the project with the given id.
     * 
     * @param projectId the id of the project
     * @param email     the email of the user
     * @throws IllegalArgumentException if no user is found with the given email,
     *                                  or no project found with the given id,
     *                                  or the user is the only organizer on the
     *                                  project
     */
    @Transactional
    public void deleteUserProject(Long projectId, String email) {
        User user = userService.getUser(email);
        Long userId = user.getId();
        deleteUserProject(projectId, userId);
    }

    /**
     * Get the roles of the user with id ´userId´ on the project with id
     * ´projectId´.
     * 
     * @param userId    the id of the user
     * @param projectId the id of the project
     * @return the list of role of the user on the project
     * @throws IllegalArgumentException if no user is found with the given id,
     *                                  or no project found with the given id
     */
    public List<String> getUserRolesForProject(Long userId, Long projectId) {
        userService.isUser(userId);
        isProject(projectId);
        List<String> res = new ArrayList<>();
        List<UserProject> userProjects = userProjectRepository.findByUserIdAndProjectId(userId, projectId);
        for (UserProject userProject : userProjects) {
            res.add(userProject.getRole());
        }
        return res;
    }

    /**
     * Get the roles of the user with email ´email´ on the project with id
     * ´projectId´.
     * 
     * @param email     the email of the user
     * @param projectId the id of the project
     * @return the list of role of the user on the project
     * @throws IllegalArgumentException if no user is found with the given email,
     *                                  or no project found with the given id
     */
    public List<String> getUserRolesForProjectByEmail(String email, Long projectId) {
        User user = userService.getUser(email);
        Long userId = user.getId();
        return getUserRolesForProject(userId, projectId);
    }

    /**
     * Remove the ´role´ from the user with id ´userId´ in the project ´projectId´
     * from the database.
     * 
     * @param projectId the id of the project
     * @param userId    the id of the user
     * @param role      the role to be delete
     * @throws IllegalArgumentException if no user is found with the given id,
     *                                  or no project found with the given id,
     *                                  or the role is "Organizer" and the user is
     *                                  the only organizer on the project
     */
    @Transactional
    public void deleteUserRole(Long projectId, Long userId, String role) {
        userService.isUser(userId);
        isProject(projectId);
        if (role.equals("Organizer")) {
            List<UserProject> organizers = userProjectRepository.findByProjectIdAndRole(projectId, "Organizer");
            if (organizers.size() == 1) {
                throw new IllegalArgumentException("Au moins un organisateur dois être présent sur le projet");
            }
        }
        List<UserProject> userProjects = userProjectRepository.findByUserIdAndProjectIdAndRole(userId, projectId, role);
        if (!userProjects.isEmpty()) {
            userProjectRepository.deleteByProjectIdAndUserIdAndRole(projectId, userId, role);
        } else {
            throw new IllegalArgumentException("Role " + role + " not found.");
        }
        userProjects = userProjectRepository.findByUserIdAndProjectId(userId, projectId);
        if (userProjects.isEmpty()) {
            Optional<Role> existingRole = roleRepository.findById("Non défini");
            if (!existingRole.isPresent()) {
                Role r = new Role("Non défini");
                roleRepository.save(r);
            }
            UserProject userProject = new UserProject(userId, projectId, "Non défini");
            userProjectRepository.save(userProject);
        }
    }

    /**
     * Add roles to the user with id ´userId´ in the project with id ´projectId´. If
     * the user add no predefined roles before the role ´Non défini´ will be removed
     * from his roles.
     * 
     * @param userId    the id of the user
     * @param projectId the id of the project
     * @param roles     the list of roles to add to the user in the project
     * @return the updated User Project
     * @throws IllegalArgumentException if no user is found with the given id,
     *                                  or no project found with the given id,
     *                                  or one of the roles does not exist
     */
    @Transactional
    public UserProjectResponse addUserRolesToUserInProject(Long userId, Long projectId, List<String> roles) {
        System.out.println("DEBUGG Received userId: " + userId);
        System.out.println("DEBUGG Received projectId: " + projectId);
        System.out.println("DEBUGG Received roles: " + roles);

        userService.isUser(userId);
        isProject(projectId);
        List<String> addedRoles = new ArrayList<>();
        for (String role : roles) {
            System.out.println("DEBUGG Received role: " + role);
            isRole(role);
            System.out.println("DEBUGG Received role " + role);
            Optional<UserProject> existingUserProject = userProjectRepository
                    .findById(new UserProjectId(userId, projectId, role));
            if (!existingUserProject.isPresent()) {
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

    /**
     * Updates the roles of the user with id ´userId´ in the project ´projectId´ to
     * the list of roles ´roles´.
     * 
     * @param userId    the id of the user
     * @param projectId the id of the project
     * @param roles     the updated roles of the user on the project
     * @throws IllegalArgumentException if no user is found with the given id,
     *                                  or no project found with the given id,
     *                                  or the role is "Organizer" and the user is
     *                                  the only organizer on the project,
     *                                  or one of the roles does not exist
     * @return the project id, user id, and the updated list of roles
     */
    @Transactional
    public UserProjectResponse updateUserRolesToUserInProject(Long userId, Long projectId, List<String> roles) {
        userService.isUser(userId);
        isProject(projectId);
        List<UserProject> existingUserProjects = userProjectRepository.findByUserIdAndProjectId(userId, projectId);
        List<String> existingRoles = existingUserProjects.stream().map(UserProject::getRole)
                .collect(Collectors.toList());
        List<String> rolesToRemove = existingRoles.stream().filter(role -> !roles.contains(role))
                .collect(Collectors.toList());
        for (String roleToRemove : rolesToRemove) {
            if (roleToRemove.equals("Organizer")) {
                List<UserProject> organizers = userProjectRepository.findByProjectIdAndRole(projectId, "Organizer");
                if (organizers.size() == 1) {
                    throw new IllegalArgumentException("Au moins un organisateur dois être présent sur le projet");
                }
            }
            userProjectRepository.deleteById(new UserProjectId(userId, projectId, roleToRemove));
        }
        List<String> roleToAdd = roles.stream().filter(role -> !existingRoles.contains(role))
                .collect(Collectors.toList());
        for (String role : roleToAdd) {
            isRole(role);
            Optional<UserProject> existingUserProject = userProjectRepository
                    .findById(new UserProjectId(userId, projectId, role));
            if (!existingUserProject.isPresent()) {
                UserProject userProject = new UserProject(userId, projectId, role);
                userProjectRepository.save(userProject);
            }
        }
        if (!roleToAdd.isEmpty() && !roleToAdd.contains("Non défini")) {
            Optional<UserProject> userProject = userProjectRepository
                    .findById(new UserProjectId(userId, projectId, "Non défini"));
            userProject.ifPresent(
                    up -> userProjectRepository.deleteById(new UserProjectId(userId, projectId, "Non défini")));
        }

        return new UserProjectResponse(userId, projectId, roles);
    }

    /**
     * Retun a map to know if the user with email ´email´ is an organizer on the
     * project with id ´id´.
     * 
     * @param email     the user email
     * @param projectId the id of the project
     * @return a map {"isOrganizer": true/false}
     * @throws IllegalArgumentException if no project is found with the given Id,
     *                                  or no user found with the given email
     */
    public Map<String, Boolean> isUserOrganizer(String email, Long projectId) {
        User user = userService.getUser(email);
        Long userId = user.getId();
        isProject(projectId);
        List<UserProject> userProjects = userProjectRepository.findByUserIdAndProjectIdAndRole(userId, projectId,
                "Organizer");
        boolean isOrganizer = false;
        if (!userProjects.isEmpty()) {
            isOrganizer = true;
        }
        Map<String, Boolean> res = new HashMap<>();
        res.put("isOrganizer", isOrganizer);
        return res;
    }

}
