package calendarapp.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import calendarapp.model.User;
import calendarapp.model.Vacation;
import calendarapp.repository.VacationRepository;
import calendarapp.request.CreateVacationRequest;
import jakarta.transaction.Transactional;

@Service
public class VacationService {

    @Autowired
    private UserService userService;
    @Autowired
    private VacationRepository vacationRepository;

    /**
     * Get the vacations of the user with email `email`. Sorted be endDate then by strat date.
     * 
     * @param email the email of the user
     * @return the list of vacations of the user
     * @throws IllegalArgumentException if no user found with the given email
     */
    public List<Vacation> getUserVacations(String email){
        User user = userService.getUser(email);
        List<Vacation> vacations = vacationRepository.findByUserId(user.getId());
        vacations.sort(Comparator
                .comparing(Vacation::getEndDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Vacation::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())));
        return vacations;
    }
    
    /**
     * Save a new vacation to the database.
     * 
     * @param vacation the vacation to be saved
     * @return the saved vacation
     * @throws IllegalArgumentException if the ending date is in the past, 
     *                                  or no user found with the given email
     */
    public Vacation createVacation(CreateVacationRequest vacation){
        User user = userService.getUser(vacation.getEmail());
        LocalDate now = LocalDate.now();
        if (vacation.getEndDate().isBefore(now)) {
            throw new IllegalArgumentException("The ending date cannot be in the past");
        }
        Vacation res = new Vacation(user.getId(), vacation.getStartDate(), vacation.getEndDate());
        vacationRepository.save(res);
        return res;
    }

    /**
     * Delete a given vacation from the database.
     * 
     * @param vacation the vacation to delete
     * @throws IllegalArgumentException if no user found with the given id
     */
    @Transactional
    public void deleteVacation(Vacation vacation){
        userService.isUser(vacation.getUserId());
        vacationRepository.delete(vacation);
    }
}
