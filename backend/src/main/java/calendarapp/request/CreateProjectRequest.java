package calendarapp.request;

import java.time.LocalDate;


public class CreateProjectRequest {
    private String name;
    private String description;
    private LocalDate beginningDate;
    private LocalDate endingDate;
    private String organizerEmail;

    public CreateProjectRequest() {
    }

    public CreateProjectRequest(String name, String description, LocalDate beginningDate, LocalDate endingDate, String organizerEmail) {
        this.name = name;
        this.description = description;
        this.beginningDate = beginningDate;
        this.endingDate = endingDate;
        this.organizerEmail = organizerEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(LocalDate beginningDate) {
        this.beginningDate = beginningDate;
    }

    public LocalDate getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDate endingDate) {
        this.endingDate = endingDate;
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public void setOrganizerEmail(String organizerEmail) {
        this.organizerEmail = organizerEmail;
    }
}
