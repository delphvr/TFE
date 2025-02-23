package calendarapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import calendarapp.model.AvailabilityValue;
import calendarapp.repository.AvailabilityValueRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class AvailabilityValueService {

    @Autowired
    private AvailabilityValueRepository availabilityValueRepository;

    /**
     * Ensures that the availability values (0, 1, 2) exist in the database when the application start.
     */
    @PostConstruct
    public void initAvailabilityValues() {
        List<Integer> defaultValues = Arrays.asList(0, 1, 2);
        for (Integer value : defaultValues) {
            if (!availabilityValueRepository.existsById(value)) {
                availabilityValueRepository.save(new AvailabilityValue(value));
            }
        }
    }
}
