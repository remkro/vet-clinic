package pl.kurs.vetclinic.error.exceptionhandling.constrainterrorhandler;

import pl.kurs.vetclinic.error.exceptionhandling.dto.ConstraintErrorDto;

public interface ConstraintErrorHandler {

    String getConstraintName();

    ConstraintErrorDto buildDto();

}
