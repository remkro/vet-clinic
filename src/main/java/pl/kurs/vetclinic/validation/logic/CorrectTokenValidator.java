package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import pl.kurs.vetclinic.repository.VisitRepository;
import pl.kurs.vetclinic.validation.annotation.CorrectToken;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

@Service
public class CorrectTokenValidator implements ConstraintValidator<CorrectToken, String> {

    private final VisitRepository visitRepository;

    public CorrectTokenValidator(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Override
    public boolean isValid(String id, ConstraintValidatorContext constraintValidatorContext) {
        UUID uuid = null;
        try{
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException exception){
            return false;
        }
        return visitRepository.existsById(uuid);
    }

}
