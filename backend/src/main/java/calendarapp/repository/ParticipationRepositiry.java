package calendarapp.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.Participation;
import calendarapp.model.ParticipationId;

public interface ParticipationRepositiry extends JpaRepository<Participation, ParticipationId> {
}

