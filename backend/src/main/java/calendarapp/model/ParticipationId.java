package calendarapp.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ParticipationId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "rehearsal_id", nullable = false)
    private Long rehearsalId;

    public ParticipationId() {
    }

    public ParticipationId(Long userId, Long rehearsalId) {
        this.userId = userId;
        this.rehearsalId = rehearsalId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRehearsalId() {
        return rehearsalId;
    }

    public void setRehearsalId(Long rehearsalId) {
        this.rehearsalId = rehearsalId;
    }

    // Override equals and hashCode for composite key
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ParticipationId that = (ParticipationId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(rehearsalId, that.rehearsalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, rehearsalId);
    }
}
