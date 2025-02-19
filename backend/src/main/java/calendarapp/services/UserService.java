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

    /**
     * Checks if a user with the given ´userId´ exists in the database.
     * If it does not exist, throws an IllegalArgumentException.
     * @param userId: the id of a user 
     * @throws IllegalArgumentException if no user is found with the given ID
     */
    public void isUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with id " + userId);
        }
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
                if (!existingProfession.isPresent()) {
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

    public boolean isUserOrganizer(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return organizerRepository.existsById(user.get().getId());
        } else {
            throw new IllegalArgumentException("User not found with email " + email);
        }
    }

}
