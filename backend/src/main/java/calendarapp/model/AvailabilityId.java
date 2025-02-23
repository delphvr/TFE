package calendarapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AvailabilityId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "beginning_date", nullable = false)
    private LocalDateTime beginningDate;

    @Column(name = "ending_date", nullable = false)
    private LocalDateTime endingDate;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    public AvailabilityId() {
    }

    public AvailabilityId(Long userId, Long projectId, LocalDateTime beginningDate, LocalDateTime endingDate) {
        this.userId = userId;
        this.projectId = projectId;
        this.beginningDate = beginningDate;
        this.endingDate = endingDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(LocalDateTime beginningDate) {
        this.beginningDate = beginningDate;
    }

    public LocalDateTime getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDateTime endingDate) {
        this.endingDate = endingDate;
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
        AvailabilityId that = (AvailabilityId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(projectId, that.projectId) &&
                Objects.equals(beginningDate, that.beginningDate) &&
                Objects.equals(endingDate, that.endingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, projectId, beginningDate, endingDate);
    }
}
