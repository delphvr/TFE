package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.RehearsalPrecedence;
import calendarapp.model.RehearsalPrecedenceId;

public interface RehearsalPrecedenceRepository extends JpaRepository<RehearsalPrecedence, RehearsalPrecedenceId> {
}
