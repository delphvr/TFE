package calendarapp.response;

import java.util.List;

public class UserProjectResponse {

    private Long userId;
    private Long projectId;
    private List<String> role;

    UserProjectResponse() {}

    public UserProjectResponse(Long userId, Long projectId, List<String> role) {
        this.userId = userId;
        this.projectId = projectId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }
    
}
