package pl.kurs.vetclinic.error.exceptionhandling.constrainterrorhandler;

import org.springframework.stereotype.Component;
import pl.kurs.vetclinic.error.exceptionhandling.dto.ConstraintErrorDto;

@Component
public class CorrectTokenConstraintErrorHandler implements ConstraintErrorHandler {

    @Override
    public String getConstraintName() {
        return "CORRECT_TOKEN_CONSTRAINT";
    }

    @Override
    public ConstraintErrorDto buildDto() {
        return new ConstraintErrorDto("INVALID_TOKEN");
    }

}
