package pl.kurs.vetclinic.error.exceptionhandling.constrainterrorhandler;

import org.springframework.stereotype.Component;
import pl.kurs.vetclinic.error.exceptionhandling.dto.ConstraintErrorDto;

@Component
public class PatientIdExistsConstraintErrorHandler implements ConstraintErrorHandler{

    @Override
    public String getConstraintName() {
        return "PATIENT_ID_EXISTS_CONSTRAINT";
    }

    @Override
    public ConstraintErrorDto buildDto() {
        return new ConstraintErrorDto("PATIENT_ID_NOT_FOUND");
    }

}
