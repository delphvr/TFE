package calendarapp.services;

import calendarapp.model.Organizer;
import calendarapp.model.Profession;
import calendarapp.model.User;
import calendarapp.model.UserProfession;
import calendarapp.repository.OrganizerRepository;
import calendarapp.repository.ProfessionRepository;
import calendarapp.repository.UserProfessionRepository;
import calendarapp.repository.UserRepository;
import calendarapp.request.CreateUserRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizerRepository organizerRepository;
    @Autowired
    private UserProfessionRepository userProfessionRepository;
    @Autowired
    private ProfessionRepository professionRepository;

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<User>();
        userRepository.findAll().forEach(users::add);
        if (users.isEmpty()) {
            throw new NoSuchElementException("No users found in the database.");
        }
        return users;
	}

    @Transactional
    public User createUser(CreateUserRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("A user with email " + request.getEmail() + " already exists.");
        }
        
        User user = new User(null, request.getFirstName(), request.getLastName(), request.getEmail());
        user = userRepository.save(user);

        if (request.getProfessions() != null && !request.getProfessions().isEmpty()) {
            for (String profession : request.getProfessions()) {
                Optional<Profession> existingProfession = professionRepository.findById(profession);
                if (!existingProfession.isPresent()){
                    Profession prof = new Profession(profession);
                    professionRepository.save(prof);
                }
               UserProfession userProfession = new UserProfession(user.getId(), profession);
                userProfession.setUser(user);
                userProfessionRepository.save(userProfession);
            }
        }
        if (request.getIsOrganizer()) {
            Organizer organizer = new Organizer(user.getId());
            organizerRepository.save(organizer);
        }
        return user;
    }

    public User updateUser(long id, User user) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            User _user = userData.get();
            _user.setFirstName(user.getFirstName());
            _user.setLastName(user.getLastName());
            _user.setEmail(user.getEmail());
            return userRepository.save(_user);
        } else {
            throw new IllegalArgumentException("User not found with id " + id);
        }
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

}
