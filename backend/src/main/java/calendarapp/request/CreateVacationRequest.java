package calendarapp.request;

import java.time.LocalDate;

public class CreateVacationRequest {
    
    private String email;
    private LocalDate startDate;
    private LocalDate endDate;

    public CreateVacationRequest(String email, LocalDate startDate, LocalDate endDate) {
        this.email = email;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public CreateVacationRequest() {
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
