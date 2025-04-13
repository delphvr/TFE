package calendarapp.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import calendarapp.model.User;
import calendarapp.model.WeeklyAvailability;
import calendarapp.repository.WeeklyAvailabilityRepository;
import calendarapp.request.CreateWeeklyAvailabilityRequest;
import jakarta.transaction.Transactional;

@Service
public class WeeklyAvailabilityService {

    @Autowired
    private UserService userService;
    @Autowired
    private WeeklyAvailabilityRepository weeklyAvailabilityRepository;

    /**
     * Does the avalability overlap with an existing avalability of the user?
     * 
     * @param availability the availability to check if it overlap with existing availabilities
     * @return true if the availability overlap with an existing availability of the user.
     */
    public boolean isOverlapping(WeeklyAvailability availability) {
        List<WeeklyAvailability> availabilities = weeklyAvailabilityRepository.findByUserIdAndWeekday(availability.getUserId(), availability.getWeekday());
        for (WeeklyAvailability weeklyAvailability : availabilities) {
            if (availability.getStartTime().isBefore(weeklyAvailability.getEndTime())
                    && availability.getEndTime().isAfter(weeklyAvailability.getStartTime())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Save the given availabilities of a user is the database.
     * 
     * @param availabilities the availability to save in the database (user email, start time, end time and the weekdays it corresponds to)
     * @return the saved availabilities
     * @throws IllegalArgumentException if no user found with the given id,
     *                                  or if the availability overlap with an
     *                                  existing availability in the database for
     *                                  that user
     */
    @Transactional
    public List<WeeklyAvailability> createAvailability(CreateWeeklyAvailabilityRequest availabilities) {
        //TODO: if had that day 15 to 18 and now add 10 to 15, merge to get 10 to 18 
        User user = userService.getUser(availabilities.getEmail());
        List<WeeklyAvailability> res = new ArrayList<>();
        for (Integer weekday : availabilities.getWeekdays()){
            WeeklyAvailability weeklyAvailability = new WeeklyAvailability(user.getId(), availabilities.getStartTime(), availabilities.getEndTime(), weekday);
            if (isOverlapping(weeklyAvailability)) {
                throw new IllegalArgumentException("Availabilities cannot overlap");
            }
            if (weeklyAvailability.getStartTime().isAfter(weeklyAvailability.getEndTime())){ //TODO check that there is this check in the frontend as well
                throw new IllegalArgumentException("The end time cannot happend before the start time");
            }
            weeklyAvailability = weeklyAvailabilityRepository.save(weeklyAvailability);
            res.add(weeklyAvailability);
        }        
        return res;
    }

    /**
     * Get the list of weekly availabilities of the user with email `email`
     * @param email the email of the user
     * @return the list of weekly availabilities of the user
     * @throws IllegalArgumentException if no user found with the given email
     */
    public List<WeeklyAvailability> getUserAvailabilities(String email){
        User user = userService.getUser(email);
        return weeklyAvailabilityRepository.findByUserId(user.getId());
    }

    /**
     * Get the list of weekly availabilities of the user with id `userId`
     * @param userId the id of the user
     * @return the list of weekly availabilities of the user
     * @throws IllegalArgumentException if no user found with the given user id
     */
    public List<WeeklyAvailability> getUserAvailabilities(Long userId){
        userService.isUser(userId);
        return weeklyAvailabilityRepository.findByUserId(userId);
    }

    /**
     * Delete an weekly avaialbility from the database.
     * 
     * @param availability the availability to delete
     * @throws IllegalArgumentException if no user found with the given id
     */
    @Transactional
    public void deleteWeeklyAvailability(WeeklyAvailability availability){
        userService.isUser(availability.getUserId());
        weeklyAvailabilityRepository.delete(availability);
    }
}
