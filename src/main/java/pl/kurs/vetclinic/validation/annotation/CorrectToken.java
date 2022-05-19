package pl.kurs.vetclinic.validation.annotation;

import pl.kurs.vetclinic.validation.logic.CorrectTokenValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CorrectTokenValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectToken {

    String message() default "CORRECT_TOKEN_CONSTRAINT";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
