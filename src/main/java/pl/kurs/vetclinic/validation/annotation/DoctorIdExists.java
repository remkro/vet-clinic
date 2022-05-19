package pl.kurs.vetclinic.validation.annotation;

import pl.kurs.vetclinic.validation.logic.DoctorIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = DoctorIdValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DoctorIdExists {

    String message() default "DOCTOR_ID_EXISTS_CONSTRAINT";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}