package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vetclinic.repository.DoctorRepository;
import pl.kurs.vetclinic.validation.annotation.Employed;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
public class EmployedValidator implements ConstraintValidator<Employed, Long> {

    private final DoctorRepository doctorRepository;

    public EmployedValidator(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return doctorRepository.getById(id).getCurrentlyEmployed();
    }

}
