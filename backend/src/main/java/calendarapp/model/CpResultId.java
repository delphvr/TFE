package calendarapp.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CpResultId implements Serializable {

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "rehearsal_id", nullable = false)
    private Long rehearsalId;

    public CpResultId() {
    }

    public CpResultId(Long projectId, Long rehearsalId) {
        this.projectId = projectId;
        this.rehearsalId = rehearsalId;
    }

    public Long getRehearsalId() {
        return rehearsalId;
    }

    public void setRehearsalId(Long rehearsalId) {
        this.rehearsalId = rehearsalId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }


    // Override equals and hashCode for composite key
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
            CpResultId that = (CpResultId) o;
        return Objects.equals(rehearsalId, that.rehearsalId) && Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rehearsalId, projectId);
    }
}
