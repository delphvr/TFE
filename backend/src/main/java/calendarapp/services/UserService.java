package calendarapp.services;

import calendarapp.model.Profession;
import calendarapp.model.User;
import calendarapp.model.UserProfession;
import calendarapp.repository.ProfessionRepository;
import calendarapp.repository.UserProfessionRepository;
import calendarapp.repository.UserRepository;
import calendarapp.request.UserRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfessionRepository userProfessionRepository;
    @Autowired
    private ProfessionRepository professionRepository;

    /**
     * Checks if a user with the given ´userId´ exists in the database.
     * If it does not exist, throws an IllegalArgumentException.
     * 
     * @param userId the id of a user
     * @throws IllegalArgumentException if no user is found with the given id
     */
    public void isUser(Long userId) {
        if(userId == null){
            throw new IllegalArgumentException("User not found with id " + userId);
        }
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with id " + userId);
        }
    }

    /**
     * Checks if a user with the given email exists in the database.
     * If it does not exist, throws an IllegalArgumentException.
     * 
     * @param email the email of a user
     * @throws IllegalArgumentException if no user is found with the given email
     */
    public void isUser(String email) {
        if(email == null){
            throw new IllegalArgumentException("User not found with email " + email);
        }
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with email " + email);
        }
    }

    /**
     * Get the user with id ´id´
     * 
     * @param id id of a user
     * @return the user with the given id
     * @throws IllegalArgumentException if no user is found with the given id
     */
    public User getUser(Long id) {
        if(id == null){
            throw new IllegalArgumentException("User not found with id " + id);
        }
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with id " + id);
        }
        return user.get();
    }

    /**
     * Get the user with email ´email´
     * 
     * @param email email of a user in a string format
     * @return the user with the given email
     * @throws IllegalArgumentException if no user is found with the given email
     */
    public User getUser(String email) {
        if(email == null){
            throw new IllegalArgumentException("User not found with email " + email);
        }
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with email " + email);
        }
        return user.get();
    }

    /**
     * Checks if a profession exists in the database.
     * If it does not exist, throws an IllegalArgumentException.
     * 
     * @param profession: the profession
     * @throws IllegalArgumentException if the profession is not found
     */
    private void isProfession(String profession) {
        Optional<Profession> existingProfession = professionRepository.findById(profession);
        if (!existingProfession.isPresent()) {
            throw new IllegalArgumentException("Profession " + profession + " not found.");
        }
    }

    /**
     * Create a user and save it in the data base.
     * 
     * @param request the data to create the user (first name, last name, email, professions)
     * @return the newly created user
     * @throws IllegalArgumentException if one of the proffesions does not exist
     */
    @Transactional
    public User createUser(UserRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("A user with email " + request.getEmail() + " already exists.");
        }

        User user = new User(null, request.getFirstName(), request.getLastName(), request.getEmail());
        user = userRepository.save(user);

        if (request.getProfessions() != null && !request.getProfessions().isEmpty()) {
            for (String profession : request.getProfessions()) {
                isProfession(profession);
                UserProfession userProfession = new UserProfession(user.getId(), profession);
                userProfession.setUser(user);
                userProfessionRepository.save(userProfession);
            }
        }
        return user;
    }

    /**
     * Update inforamition related to the user with id ´id´ in the database
     * 
     * @param id      the id of the user to update
     * @param request UserRequest object containing the data about the user to
     *                update
     * @return the updated user
     * @throws IllegalArgumentException if no user found with the given id,
     *                                  or if one of the proffesions does not exist
     */
    @Transactional
    public User updateUser(long id, UserRequest request) {
        User _user = getUser(id);
        _user.setFirstName(request.getFirstName());
        _user.setLastName(request.getLastName());
        _user.setEmail(request.getEmail());
        List<UserProfession> existingUserProfessions = userProfessionRepository.findByUserId(_user.getId());
        List<String> existingProfessions = existingUserProfessions.stream().map(UserProfession::getProfession)
                .collect(Collectors.toList());
        List<String> updatedProfessions = request.getProfessions();
        // Delet professions not present anymore
        for (UserProfession userProfession : existingUserProfessions) {
            if (!updatedProfessions.contains(userProfession.getProfession())) {
                userProfessionRepository.delete(userProfession);
            }
        }
        // add new professions
        for (String profession : updatedProfessions) {
            if (!existingProfessions.contains(profession)) {
                isProfession(profession);
                UserProfession newUserProfession = new UserProfession(_user.getId(), profession);
                userProfessionRepository.save(newUserProfession);
            }
        }
        return userRepository.save(_user);
    }

    /**
     * Get the list of the user professions
     * 
     * @param email email of the user
     * @return a list of string representing all the user professions
     * @throws IllegalArgumentException if no user is found with the given email
     */
    public List<String> getUserProfessions(String email) {
        List<String> res = new ArrayList<>();
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with email " + email);
        }
        List<UserProfession> userProfessions = userProfessionRepository.findByUserId(user.get().getId());
        for (UserProfession userProfession : userProfessions) {
            res.add(userProfession.getProfession());
        }
        return res;
    }

    /**
     * Delete user completely drom the database
     * 
     * @param email email of the user to be delete
     * @throws IllegalArgumentException if no user is found with the given email
     */
    @Transactional
    public void deleteByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found with email " + email);
        }
        userRepository.deleteByEmail(email);
    }

}
