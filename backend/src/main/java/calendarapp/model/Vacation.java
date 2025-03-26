package calendarapp.model;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "vacations")
@IdClass(VacationId.class)
public class Vacation {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Id
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    public Vacation() {
    }

    public Vacation(Long userId, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
