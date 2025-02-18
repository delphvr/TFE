package calendarapp.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "rehearsals_precedences")
@IdClass(RehearsalPrecedenceId.class)
public class RehearsalPrecedence {

    @Id
    @Column(name = "current", nullable = false)
    private Long current;

    @Id
    @Column(name = "previous", nullable = false)
    private Long previous;

    @ManyToOne
    @JoinColumn(name = "current", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Rehearsal currentRehearsal;

    @ManyToOne
    @JoinColumn(name = "previous", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Rehearsal previousRehearsal;

    public RehearsalPrecedence() {}

    public RehearsalPrecedence(Long current, Long previous) {
        this.current = current;
        this.previous = previous;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getPrevious() {
        return previous;
    }

    public void setPrevious(Long previous) {
        this.previous = previous;
    }
}
