package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import pl.kurs.vetclinic.repository.DoctorRepository;
import pl.kurs.vetclinic.validation.annotation.UniqueNip;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
public class UniqueNipValidator implements ConstraintValidator<UniqueNip, String> {

    private final DoctorRepository doctorRepository;

    public UniqueNipValidator(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public boolean isValid(String nip, ConstraintValidatorContext constraintValidatorContext) {
        return !doctorRepository.existsByNip(nip);
    }

}
