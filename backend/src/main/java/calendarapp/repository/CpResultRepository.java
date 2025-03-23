package calendarapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.CpResult;
import calendarapp.model.CpResultId;

public interface CpResultRepository extends JpaRepository<CpResult, CpResultId>{
    List<CpResult> findByProjectId(Long projectId);
}