package calendarapp.request;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class RehearsalRequest {
    private String name;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private Duration duration;
    private Long projectId;
    private List<Long> participantsIds;
    private String location;

    public RehearsalRequest() {}

    public RehearsalRequest(String name, String description, LocalDate date, LocalTime time, Duration duration, Long projectId, List<Long> participantsIds, String location) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.projectId = projectId;
        this.participantsIds = participantsIds;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public Duration getDuration() {
        return duration;
    }

    public Long getProjectId() {
        return projectId;
    }

    public List<Long> getParticipantsIds() {
        return participantsIds;
    }

    public String getLocation() {
        return location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setParticipantsIds(List<Long> participantsIds) {
        this.participantsIds = participantsIds;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}

