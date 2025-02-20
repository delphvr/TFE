package calendarapp.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.Participation;
import calendarapp.model.ParticipationId;

public interface ParticipationRepositiry extends JpaRepository<Participation, ParticipationId> {
    List<Participation> findByRehearsalId(Long rehearsalId);
}

