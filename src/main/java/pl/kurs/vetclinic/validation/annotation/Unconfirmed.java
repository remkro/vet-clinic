package pl.kurs.vetclinic.validation.annotation;

import pl.kurs.vetclinic.validation.logic.CorrectTokenValidator;
import pl.kurs.vetclinic.validation.logic.UnconfirmedVisitValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = UnconfirmedVisitValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Unconfirmed {

    String message() default "UNCONFIRMED_CONSTRAINT";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
