package calendarapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import calendarapp.model.UserProject;
import calendarapp.model.UserProjectId;

public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {
    List<UserProject> findByUserIdAndRole(Long userId, String role);
    List<UserProject> findByUserId(Long userId);
}
