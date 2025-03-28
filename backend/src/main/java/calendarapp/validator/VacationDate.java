package calendarapp.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = VacationDatesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public  @interface VacationDate {
    String message() default "{La date de fin ne peut pas être avant la date de début.}";
  Class <?> [] groups() default {};
  Class <? extends Payload> [] payload() default {};
}


