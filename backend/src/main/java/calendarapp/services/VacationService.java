package calendarapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import calendarapp.model.User;
import calendarapp.model.Vacation;
import calendarapp.repository.VacationRepository;

@Service
public class VacationService {

    @Autowired
    private UserService userService;
    @Autowired
    private VacationRepository vacationRepository;

    /**
     * Get the vacations of the user with email `email`.
     * 
     * @param email the email of the user
     * @return the list of vacations of the user
     * @throws IllegalArgumentException if no user found with the given email
     */
    public List<Vacation> getUserVacations(String email){
        User user = userService.getUser(email);
        return vacationRepository.findByUserId(user.getId());
    }
    
    /**
     * Save a new vacation to the database.
     * 
     * @param vacation the vacation to be saved
     * @return the saved vacation
     * @throws IllegalArgumentException if no user found with the given id
     */
    public Vacation createVacation(Vacation vacation){
        userService.isUser(vacation.getUserId());
        Vacation res = vacationRepository.save(vacation);
        return res;
    }
}
