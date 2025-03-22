package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.CpResult;
import calendarapp.model.CpResultId;

public interface CpResultRepository extends JpaRepository<CpResult, CpResultId>{
}