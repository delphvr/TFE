package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.Availability;
import calendarapp.model.AvailabilityId;

public interface AvailabilityRepository extends JpaRepository<Availability, AvailabilityId>{
}