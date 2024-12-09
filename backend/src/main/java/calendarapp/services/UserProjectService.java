package calendarapp.services;

import calendarapp.model.Organizer;
import calendarapp.model.Role;
import calendarapp.model.User;
import calendarapp.model.UserProject;
import calendarapp.model.UserProjectId;
import calendarapp.repository.OrganizerRepository;
import calendarapp.repository.RoleRepository;
import calendarapp.repository.UserProjectRepository;
import calendarapp.repository.UserRepository;
import calendarapp.request.CreateUserRoleRequest;

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

    public List<UserProject> getAllUserProjects() {
        List<UserProject> userProjects = new ArrayList<UserProject>();
        userProjectRepository.findAll().forEach(userProjects::add);
        if (userProjects.isEmpty()) {
            throw new NoSuchElementException("No users project relations found in the database.");
        }
        return userProjects;
    }

    public List<UserProject> createUserProject(CreateUserRoleRequest request) {
        List<UserProject> res= new ArrayList<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String role : request.getRoles()) {
                Optional<Role> existingRole = roleRepository.findById(role);
                if (!existingRole.isPresent()) {
                    Role r = new Role(role);
                    roleRepository.save(r);
                }
                if (role == "Organizer"){
                    Organizer organizer = new Organizer(request.getUserId());
                    organizerRepository.save(organizer);
                }
                UserProject userProject = new UserProject(request.getUserId(), request.getProjectId(), role);
                userProject = userProjectRepository.save(userProject);
                res.add(userProject);
            }
        }
        return res;
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
}
