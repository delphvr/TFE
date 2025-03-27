package calendarapp.request;

import java.time.LocalTime;
import java.util.List;

public class CreateWeeklyAvailabilityRequest {

    private String email;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<Integer> weekdays;

    public CreateWeeklyAvailabilityRequest() {
    }

    public CreateWeeklyAvailabilityRequest(String email, LocalTime startTime, LocalTime endTime, List<Integer> weekdays) {
        this.email = email;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekdays = weekdays;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<Integer> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(List<Integer> weekdays) {
        this.weekdays = weekdays;
    }
}
