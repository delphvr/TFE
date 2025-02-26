package calendarapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import calendarapp.model.UserProject;
import calendarapp.model.UserProjectId;

public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {
    List<UserProject> findByUserIdAndRole(Long userId, String role);
    List<UserProject> findByUserId(Long userId);
    List<UserProject> findByProjectId(Long projectId);
    List<UserProject> findByUserIdAndProjectId(Long userId, Long projectId);
    List<UserProject> findByProjectIdAndRole(Long projectId, String role);
    List<UserProject> findByUserIdAndProjectIdAndRole(Long userId, Long projectId, String role);
    List<UserProject> deleteByProjectIdAndUserId(Long projectId, Long userId);
    List<UserProject> deleteByProjectIdAndUserIdAndRole(Long projectId, Long userId, String role);
}
