package pl.kurs.vetclinic.validation.annotation;

import pl.kurs.vetclinic.validation.logic.NotPastValidator;
import pl.kurs.vetclinic.validation.logic.SupportedMedicalSpecializationValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SupportedMedicalSpecializationValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportedMedicalSpecialization {

    String message() default "Medical specialization is not supported";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
