package calendarapp.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

//Source: https://medium.com/@tericcabrel/write-custom-validator-for-request-body-in-spring-boot-31a6aa5e53b1

@Constraint(validatedBy = ProjectDatesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectDate {
  String message() default "{La date de fin ne peut pas être avant la date de début.}";
  Class <?> [] groups() default {};
  Class <? extends Payload> [] payload() default {};
}
