package calendarapp.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CpPresenceResultId implements Serializable {

    @Column(name = "rehearsal_id", nullable = false)
    private Long rehearsalId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public CpPresenceResultId() {
    }

    public CpPresenceResultId(Long rehearsalId, Long userId) {
        this.rehearsalId = rehearsalId;
        this.userId = userId;
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


    // Override equals and hashCode for composite key
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
            CpPresenceResultId that = (CpPresenceResultId) o;
        return Objects.equals(rehearsalId, that.rehearsalId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rehearsalId, userId);
    }
}