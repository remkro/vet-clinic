package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import pl.kurs.vetclinic.model.enums.MedType;
import pl.kurs.vetclinic.validation.annotation.SupportedMedicalSpecialization;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
public class SupportedMedicalSpecializationValidator implements ConstraintValidator<SupportedMedicalSpecialization, String> {

    @Override
    public boolean isValid(String specialization, ConstraintValidatorContext constraintValidatorContext) {
        MedType medType = MedType.setFromString(specialization);
        return medType != null;
    }

}
