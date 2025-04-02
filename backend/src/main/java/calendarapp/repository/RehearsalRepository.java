package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.Rehearsal;

import java.time.LocalDate;
import java.util.List;


public interface RehearsalRepository extends JpaRepository<Rehearsal, Long> {
    List<Rehearsal> findByProjectId(Long projectId);
    List<Rehearsal> findByUserIdAndDate(Long userId, LocalDate date);
}
