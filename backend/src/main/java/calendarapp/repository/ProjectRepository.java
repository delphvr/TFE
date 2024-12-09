package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
