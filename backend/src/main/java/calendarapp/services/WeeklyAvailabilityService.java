package calendarapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import calendarapp.model.WeeklyAvailability;
import calendarapp.repository.AvailabilityRepository;

@Service
public class WeeklyAvailabilityService {

    @Autowired
    private UserService userService;
    @Autowired
    private AvailabilityRepository availabilityRepository;

    /**
     * Save the availability is the database if the userid, project id and
     * availability value is correct
     * 
     * @param availability the availability to save in the database
     * @return the saved availability
     * @throws IllegalArgumentException if no user found with the given id
     */
    public WeeklyAvailability createAvailability(WeeklyAvailability availability) {
        userService.isUser(availability.getUserId());
        WeeklyAvailability res = availabilityRepository.save(availability);
        return res;
    }
}
