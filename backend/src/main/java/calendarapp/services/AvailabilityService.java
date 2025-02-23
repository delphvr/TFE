package calendarapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import calendarapp.model.Availability;
import calendarapp.repository.AvailabilityRepository;

@Service
public class AvailabilityService {

    @Autowired
    private ProjectService projectService;
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
     * @throws IllegalArgumentException if no project is found with the given Id
     *                                  or no user found with the given id
     */
    public Availability createAvailability(Availability availability) {
        userService.isUser(availability.getUserId());
        projectService.isProject(availability.getProjectId());
        Availability res = availabilityRepository.save(availability);
        return res;
    }
}
