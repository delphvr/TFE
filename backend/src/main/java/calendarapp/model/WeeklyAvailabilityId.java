package calendarapp.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class WeeklyAvailabilityId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "weekday", nullable = false)
    private int weekday;

    public WeeklyAvailabilityId() {
    }

    public WeeklyAvailabilityId(Long userId, LocalTime startTime, LocalTime endTime, int weekday) {
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekday = weekday;
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

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WeeklyAvailabilityId that = (WeeklyAvailabilityId) o;
        return weekday == that.weekday &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, startTime, endTime, weekday);
    }
}
