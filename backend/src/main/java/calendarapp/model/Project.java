package calendarapp.model;

import java.time.LocalDate;

import calendarapp.validator.ProjectDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "projects")
@ProjectDate
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank()
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "beginning_date", nullable = false)
    private LocalDate beginningDate;

    @Column(name = "ending_date", nullable = false)
    private LocalDate endingDate;

    public Project() {
    }

    public Project(Long id, String name, String description, LocalDate beginningDate, LocalDate endingDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.beginningDate = beginningDate;
        this.endingDate = endingDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
