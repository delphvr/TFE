package calendarapp.validator;

import calendarapp.model.Vacation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VacationDatesValidator implements ConstraintValidator<VacationDate, Vacation> {
    @Override
    public boolean isValid(Vacation vacation, ConstraintValidatorContext context) {
        return !vacation.getEndDate().isBefore(vacation.getStartDate());
    }
    
}
