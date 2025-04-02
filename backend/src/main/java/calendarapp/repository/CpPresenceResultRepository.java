package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.CpPresenceResult;
import calendarapp.model.CpPresenceResultId;

public interface CpPresenceResultRepository extends JpaRepository<CpPresenceResult, CpPresenceResultId>{
}

