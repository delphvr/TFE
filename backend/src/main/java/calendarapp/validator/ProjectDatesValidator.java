package calendarapp.validator;

import calendarapp.model.Project;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

//Source: https://medium.com/@tericcabrel/write-custom-validator-for-request-body-in-spring-boot-31a6aa5e53b1

public class ProjectDatesValidator implements ConstraintValidator<ProjectDate, Project> {
    @Override
    public boolean isValid(Project project, ConstraintValidatorContext context) {
        return !project.getEndingDate().isBefore(project.getBeginningDate());
    }
    
}