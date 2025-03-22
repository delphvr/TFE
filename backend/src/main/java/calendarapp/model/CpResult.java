package calendarapp.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cp_results")
@IdClass(CpResultId.class)
public class CpResult {

    @Id
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Id
    @Column(name = "rehearsal_id", nullable = false)
    private Long rehearsalId;

    @Column(name = "accepted", nullable = false)
    private boolean accepted;

    @Column(name = "beginning_date", nullable = false)
    private LocalDateTime beginningDate;    

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "rehearsal_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Rehearsal rehearsal;

    public CpResult() {
    }

    public CpResult(Long projectId, Long rehearsalId, boolean accepted, LocalDateTime beginningDate) {
        this.projectId = projectId;
        this.rehearsalId = rehearsalId;
        this.accepted = accepted;
        this.beginningDate = beginningDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getRehearsalId() {
        return rehearsalId;
    }

    public void setRehearsalId(Long rehearsalId) {
        this.rehearsalId = rehearsalId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public LocalDateTime getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(LocalDateTime beginningDate) {
        this.beginningDate = beginningDate;
    }
    
}
