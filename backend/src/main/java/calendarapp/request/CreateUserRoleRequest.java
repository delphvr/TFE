package calendarapp.request;

import java.util.List;

public class CreateUserRoleRequest {
    private Long userId;
    private Long projectId;
    private List<String> roles;

    public CreateUserRoleRequest() {
    }

    public CreateUserRoleRequest(Long userId, Long projectId, List<String> roles) {
        this.userId = userId;
        this.projectId = projectId;
        this.roles = roles;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
