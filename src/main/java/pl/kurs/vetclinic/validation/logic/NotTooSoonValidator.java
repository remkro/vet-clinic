package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import pl.kurs.vetclinic.validation.annotation.NotTooSoon;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.DAYS;
import static pl.kurs.vetclinic.config.AppConstants.DATE_TIME_FORMATTER;

@Service
public class NotTooSoonValidator implements ConstraintValidator<NotTooSoon, String> {

    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
        DateTimeFormatter formatter = DATE_TIME_FORMATTER;
        LocalDateTime parsedDate = LocalDateTime.parse(date, formatter);
        LocalDate dateWithoutTime = LocalDate.from(parsedDate);
        LocalDate nowWithoutTime = LocalDate.from(LocalDate.now());
        long days = DAYS.between(nowWithoutTime, dateWithoutTime);
        return days > 1;
    }

}
