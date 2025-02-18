package calendarapp.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class RehearsalPrecedenceId implements Serializable {

    @Column(name = "current", nullable = false)
    private Long current;

    @Column(name = "previous", nullable = false)
    private Long previous;

    public RehearsalPrecedenceId() {
    }

    public RehearsalPrecedenceId(Long current, Long previous) {
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

    // Override equals and hashCode for composite key
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RehearsalPrecedenceId that = (RehearsalPrecedenceId) o;
        return Objects.equals(current, that.current) && Objects.equals(previous, that.previous);
    }

    @Override
    public int hashCode() {
        return Objects.hash(current, previous);
    }
}
