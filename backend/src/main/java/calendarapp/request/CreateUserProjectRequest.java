package calendarapp.request;

import java.util.List;

import jakarta.validation.constraints.Email;

public class CreateUserProjectRequest {
    @Email
    private String userEmail;
    private Long projectId;
    private List<String> roles;

    public CreateUserProjectRequest() {
    }

    public CreateUserProjectRequest(String userEmail, Long projectId, List<String> roles) {
        this.userEmail = userEmail;
        this.projectId = projectId;
        this.roles = roles;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserId(String userEmail) {
        this.userEmail = userEmail;
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
