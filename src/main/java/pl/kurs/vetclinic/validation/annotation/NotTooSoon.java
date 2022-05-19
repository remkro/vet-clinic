package pl.kurs.vetclinic.validation.annotation;

import pl.kurs.vetclinic.validation.logic.NotTooSoonValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = NotTooSoonValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotTooSoon {

    String message() default "Visit cannot be booked in past";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
