package pl.kurs.vetclinic.validation.annotation;

import pl.kurs.vetclinic.validation.logic.CorrectNipValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CorrectNipValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectNip {

    String message() default "Provided NIP is not correct";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
