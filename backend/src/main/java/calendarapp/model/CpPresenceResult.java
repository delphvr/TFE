package calendarapp.model;

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
@Table(name = "cp_presence")
@IdClass(CpPresenceResultId.class)
public class CpPresenceResult {
    @Id
    @Column(name = "rehearsal_id", nullable = false)
    private Long rehearsalId;

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "present", nullable = false)
    private boolean present;

    @ManyToOne
    @JoinColumn(name = "rehearsal_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Rehearsal rehearsal;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public CpPresenceResult() {
    }

    public CpPresenceResult(Long rehearsalId, Long userId, boolean present) {
        this.rehearsalId = rehearsalId;
        this.userId = userId;
        this.present = present;
    }

    public Long getRehearsalId() {
        return rehearsalId;
    }

    public void setRehearsalId(Long rehearsalId) {
        this.rehearsalId = rehearsalId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

}
