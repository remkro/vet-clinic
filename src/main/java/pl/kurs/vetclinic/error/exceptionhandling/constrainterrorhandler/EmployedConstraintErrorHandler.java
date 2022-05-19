package pl.kurs.vetclinic.error.exceptionhandling.constrainterrorhandler;

import org.springframework.stereotype.Component;
import pl.kurs.vetclinic.error.exceptionhandling.dto.ConstraintErrorDto;

@Component
public class EmployedConstraintErrorHandler implements ConstraintErrorHandler {

    @Override
    public String getConstraintName() {
        return "EMPLOYED_CONSTRAINT";
    }

    @Override
    public ConstraintErrorDto buildDto() {
        return new ConstraintErrorDto("CANNOT_FIRE_UNEMPLOYED_PERSON");
    }

}
