package calendarapp.response;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class RehearsalResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private Duration duration;
    private Long projectId;
    private String location;
    private List<Long> participantsIds;

    public RehearsalResponse() {}

    public RehearsalResponse(Long id, String name, String description, LocalDate date, LocalTime time, Duration duration, Long projectId, String location, List<Long> participantsIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.projectId = projectId;
        this.participantsIds = participantsIds;
        this.location = location;
    }

    public Long getId(){
        return id;
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

    public Duration getDuration() {
        return duration;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getLocation() {
        return location;
    }

    public List<Long> getParticipantsIds() {
        return participantsIds;
    }

    public void setId(Long id){
        this.id = id;
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

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setParticipantsIds(List<Long> participantsIds) {
        this.participantsIds = participantsIds;
    }
    
}
