package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vetclinic.model.entity.Visit;
import pl.kurs.vetclinic.repository.VisitRepository;
import pl.kurs.vetclinic.validation.annotation.NotTooLate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotTooLateValidator implements ConstraintValidator<NotTooLate, String> {

    private final VisitRepository visitRepository;

    public NotTooLateValidator(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isValid(String id, ConstraintValidatorContext constraintValidatorContext) {
        UUID uuid = UUID.fromString(id);
        Visit visit = visitRepository.getById(uuid);
        LocalDateTime date = visit.getDate();
        return LocalDateTime.now().isBefore(date.minusDays(1));
    }

}
