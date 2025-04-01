package calendarapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.RehearsalPrecedence;
import calendarapp.model.RehearsalPrecedenceId;

public interface RehearsalPrecedenceRepository extends JpaRepository<RehearsalPrecedence, RehearsalPrecedenceId> {
    List<RehearsalPrecedence> findByCurrent(Long current);
}
