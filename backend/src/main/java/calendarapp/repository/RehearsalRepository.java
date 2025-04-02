package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.Rehearsal;

import java.util.List;


public interface RehearsalRepository extends JpaRepository<Rehearsal, Long> {
    List<Rehearsal> findByProjectId(Long projectId);
}
