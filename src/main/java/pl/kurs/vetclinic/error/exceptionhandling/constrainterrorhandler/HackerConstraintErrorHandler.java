package pl.kurs.vetclinic.error.exceptionhandling.constrainterrorhandler;

import org.springframework.stereotype.Component;
import pl.kurs.vetclinic.error.exceptionhandling.dto.ConstraintErrorDto;

@Component
public class HackerConstraintErrorHandler implements ConstraintErrorHandler {

    @Override
    public String getConstraintName() {
        return "HACKER_CONSTRAINT";
    }

    @Override
    public ConstraintErrorDto buildDto() {
        return new ConstraintErrorDto("FUCK_YOU_MR_HACKER");
    }

}