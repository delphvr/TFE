package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.CpPresenceResultId;
import calendarapp.model.RehearsalPresence;

public interface RehearsalPresenceRepository extends JpaRepository<RehearsalPresence, CpPresenceResultId>{
}

