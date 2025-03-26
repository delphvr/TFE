package calendarapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.Vacation;
import calendarapp.model.VacationId;

public interface VacationRepository extends JpaRepository<Vacation, VacationId>{
    List<Vacation> findByUserId(long userId);
}