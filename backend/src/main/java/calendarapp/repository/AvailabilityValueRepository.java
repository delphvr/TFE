package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.AvailabilityValue;

public interface AvailabilityValueRepository extends JpaRepository<AvailabilityValue, Integer>{
}
