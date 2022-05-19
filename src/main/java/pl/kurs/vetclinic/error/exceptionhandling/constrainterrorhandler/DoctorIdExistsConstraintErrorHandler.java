package pl.kurs.vetclinic.error.exceptionhandling.constrainterrorhandler;

import org.springframework.stereotype.Component;
import pl.kurs.vetclinic.error.exceptionhandling.dto.ConstraintErrorDto;

@Component
public class DoctorIdExistsConstraintErrorHandler implements ConstraintErrorHandler{

    @Override
    public String getConstraintName() {
        return "DOCTOR_ID_EXISTS_CONSTRAINT";
    }

    @Override
    public ConstraintErrorDto buildDto() {
        return new ConstraintErrorDto("DOCTOR_ID_NOT_FOUND");
    }
}
