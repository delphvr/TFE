package calendarapp.response;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class RehearsalResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDate date;
    private Duration duration;
    private Long projectId;
    private List<Long> participantsIds;

    public RehearsalResponse() {}

    public RehearsalResponse(Long id, String name, String description, LocalDate date, Duration duration, Long projectId, List<Long> participantsIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.duration = duration;
        this.projectId = projectId;
        this.participantsIds = participantsIds;
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

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setParticipantsIds(List<Long> participantsIds) {
        this.participantsIds = participantsIds;
    }
    
}
