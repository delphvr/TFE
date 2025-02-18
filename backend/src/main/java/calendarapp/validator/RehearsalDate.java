package calendarapp.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RehearsalDateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RehearsalDate {
    String message() default "Rehearsal date must be within the project's start and end date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

