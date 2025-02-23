package calendarapp.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "availabilities")
@IdClass(AvailabilityId.class)
public class Availability {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "beginning_date", nullable = false)
    private LocalDateTime beginningDate;

    @Id
    @Column(name = "ending_date", nullable = false)
    private LocalDateTime endingDate;

    @Id
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "availability", nullable = false)
    private int availability;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "availability", insertable = false, updatable = false)
    private AvailabilityValue availabilityEntity;

    public Availability() {
    }

    public Availability(Long userId, Long projectId, LocalDateTime beginningDate, LocalDateTime endingDate) {
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
}
