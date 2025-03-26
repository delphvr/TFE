package calendarapp.model;

import java.time.LocalTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "weekly_availabilities")
@IdClass(WeeklyAvailabilityId.class)
public class WeeklyAvailability {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Id
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "weekday", nullable = false)
    @Min(0)
    @Max(6)
    private int weekday; //0 (manday) - 6 (sunday)

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public WeeklyAvailability() {
    }

    public WeeklyAvailability(Long userId, LocalTime stratTime, LocalTime endTime) {
        this.userId = userId;
        this.startTime = stratTime;
        this.endTime = endTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime starTime) {
        this.startTime = starTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

}
