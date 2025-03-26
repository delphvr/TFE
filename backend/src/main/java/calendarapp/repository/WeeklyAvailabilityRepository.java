package calendarapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.WeeklyAvailability;
import calendarapp.model.WeeklyAvailabilityId;

public interface WeeklyAvailabilityRepository extends JpaRepository<WeeklyAvailability, WeeklyAvailabilityId>{
    List<WeeklyAvailability> findByUserIdAndWeekday(long userId, int weekday);
    List<WeeklyAvailability> findByUserId(long userId);
}