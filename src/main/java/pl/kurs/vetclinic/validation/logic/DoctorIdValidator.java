package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import pl.kurs.vetclinic.repository.DoctorRepository;
import pl.kurs.vetclinic.validation.annotation.DoctorIdExists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
public class DoctorIdValidator implements ConstraintValidator<DoctorIdExists, Long> {

    private final DoctorRepository doctorRepository;

    public DoctorIdValidator(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return doctorRepository.existsById(id);
    }

}
