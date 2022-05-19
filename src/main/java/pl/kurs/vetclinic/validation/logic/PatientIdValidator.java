package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import pl.kurs.vetclinic.repository.PatientRepository;
import pl.kurs.vetclinic.validation.annotation.PatientIdExists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
public class PatientIdValidator implements ConstraintValidator<PatientIdExists, Long> {

    private final PatientRepository patientRepository;

    public PatientIdValidator(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return patientRepository.existsById(id);
    }

}