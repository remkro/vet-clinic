package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import pl.kurs.vetclinic.validation.annotation.WorkingHours;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static pl.kurs.vetclinic.config.AppConstants.DATE_TIME_FORMATTER;

@Service
public class WorkingHoursValidator implements ConstraintValidator<WorkingHours, String> {

    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime parsedDate = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        switch (parsedDate.getDayOfWeek()) {
            case MONDAY:
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
            case FRIDAY:
                return !(parsedDate.getHour() < 9) && !(parsedDate.getHour() > 16);
            case SATURDAY:
                return !(parsedDate.getHour() < 9) && !(parsedDate.getHour() > 13);
            case SUNDAY:
                return false;
            default:
                return true;
        }
    }

}
