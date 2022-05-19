package pl.kurs.vetclinic.validation.annotation;

import pl.kurs.vetclinic.validation.logic.NotTooLateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = NotTooLateValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotTooLate {

    String message() default "NOT_TOO_LATE_CONSTRAINT";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
