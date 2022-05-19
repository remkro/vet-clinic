package pl.kurs.vetclinic.error.exceptionhandling.constrainterrorhandler;

import org.springframework.stereotype.Component;
import pl.kurs.vetclinic.error.exceptionhandling.dto.ConstraintErrorDto;

@Component
public class NotTooLateConstraintErrorHandler implements ConstraintErrorHandler {

    @Override
    public String getConstraintName() {
        return "NOT_TOO_LATE_CONSTRAINT";
    }

    @Override
    public ConstraintErrorDto buildDto() {
        return new ConstraintErrorDto("TOO_LATE_TO_CONFIRM_VISIT");
    }

}
