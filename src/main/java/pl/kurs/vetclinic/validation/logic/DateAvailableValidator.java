package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vetclinic.model.entity.Visit;
import pl.kurs.vetclinic.repository.VisitRepository;
import pl.kurs.vetclinic.validation.annotation.DateAvailable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static pl.kurs.vetclinic.config.AppConstants.DATE_TIME_FORMATTER;

@Service
public class DateAvailableValidator implements ConstraintValidator<DateAvailable, String> {

    private final VisitRepository visitRepository;

    public DateAvailableValidator(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime parsedDate = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        List<Visit> visitsAfterNow = visitRepository
                .findAll()
//                .getAllWithPessimisticLocking()
                .stream()
                .filter(v -> v.getDate().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (visitsAfterNow.size() == 0)
            return true;
        for (Visit visit : visitsAfterNow) {
            if (!(parsedDate.plusHours(1).isBefore(visit.getDate()) || parsedDate.isAfter(visit.getDate().plusHours(1))))
                return false;
        }
        return true;
    }

}
