package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import pl.kurs.vetclinic.validation.annotation.NotPast;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

import static pl.kurs.vetclinic.config.AppConstants.DATE_TIME_FORMATTER;

@Service
public class NotPastValidator implements ConstraintValidator<NotPast, String> {

    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime parsedDate = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        return parsedDate.isAfter(LocalDateTime.now());
    }

}
