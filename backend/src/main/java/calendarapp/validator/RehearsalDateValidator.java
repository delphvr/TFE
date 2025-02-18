package calendarapp.validator;

import calendarapp.model.Rehearsal;
import calendarapp.repository.ProjectRepository;
import calendarapp.model.Project;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class RehearsalDateValidator implements ConstraintValidator<RehearsalDate, Rehearsal> {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public boolean isValid(Rehearsal rehearsal, ConstraintValidatorContext context) {
        try {
            if (rehearsal.getDate() == null || rehearsal.getProjectId() == null) {
                return true;
            }
            Optional<Project> project = projectRepository.findById(rehearsal.getProjectId());
            if (!project.isPresent()) {
                return true; 
            }
            System.out.println("DEBUGG : " + project.get().getBeginningDate());
            System.out.println("DEBUGG : " + rehearsal.getDate());
            System.out.println("DEBUGG : " + rehearsal.getDate().isBefore(project.get().getBeginningDate()));
            if(project.get().getBeginningDate() == null && project.get().getEndingDate() == null){
                return true;
            }
            if (project.get().getEndingDate() == null){
                return !rehearsal.getDate().isBefore(project.get().getBeginningDate());
            }
            if(project.get().getBeginningDate() == null){
                return  !rehearsal.getDate().isAfter(project.get().getEndingDate());
            }
            return !rehearsal.getDate().isBefore(project.get().getBeginningDate()) &&
                   !rehearsal.getDate().isAfter(project.get().getEndingDate());
        } catch (Exception e) {
            System.out.println("DEBUGG : " + e.getMessage());
            return false;
        }
        
    }
}