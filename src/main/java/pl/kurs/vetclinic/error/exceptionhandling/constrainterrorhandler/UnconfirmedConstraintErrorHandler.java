package pl.kurs.vetclinic.error.exceptionhandling.constrainterrorhandler;

import org.springframework.stereotype.Component;
import pl.kurs.vetclinic.error.exceptionhandling.dto.ConstraintErrorDto;

@Component
public class UnconfirmedConstraintErrorHandler implements ConstraintErrorHandler {

    @Override
    public String getConstraintName() {
        return "UNCONFIRMED_CONSTRAINT";
    }

    @Override
    public ConstraintErrorDto buildDto() {
        return new ConstraintErrorDto("VISIT_ALREADY_CONFIRMED");
    }

}
