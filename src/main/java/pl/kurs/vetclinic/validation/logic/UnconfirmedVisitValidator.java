package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vetclinic.repository.VisitRepository;
import pl.kurs.vetclinic.validation.annotation.Unconfirmed;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

@Service
public class UnconfirmedVisitValidator implements ConstraintValidator<Unconfirmed, String> {

    private final VisitRepository visitRepository;

    public UnconfirmedVisitValidator(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isValid(String id, ConstraintValidatorContext constraintValidatorContext) {
        UUID uuid = UUID.fromString(id);
        return !visitRepository.getById(uuid).getConfirmed();
    }

}
