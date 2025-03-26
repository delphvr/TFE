package calendarapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import calendarapp.model.User;
import calendarapp.model.WeeklyAvailability;
import calendarapp.repository.WeeklyAvailabilityRepository;

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
     * Save the availability of a user is the database.
     * 
     * @param availability the availability to save in the database
     * @return the saved availability
     * @throws IllegalArgumentException if no user found with the given id,
     *                                  or if the availability overlap with an
     *                                  existing availability in the database for
     *                                  that user
     */
    public WeeklyAvailability createAvailability(WeeklyAvailability availability) {
        userService.isUser(availability.getUserId());
        WeeklyAvailability res = weeklyAvailabilityRepository.save(availability);
        if (isOverlapping(availability)) {
            throw new IllegalArgumentException("Availabilities cannot overlap"); // TODO to catch in front end so do not
                                                                                 // compute twice
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
}
