package calendarapp.repository;

import calendarapp.model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizerRepository extends JpaRepository<Organizer, Long> {
    boolean existsByUserId(Long userId);
}
