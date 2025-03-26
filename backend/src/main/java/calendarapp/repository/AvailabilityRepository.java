package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.WeeklyAvailability;
import calendarapp.model.WeeklyAvailabilityId;

public interface AvailabilityRepository extends JpaRepository<WeeklyAvailability, WeeklyAvailabilityId>{
}