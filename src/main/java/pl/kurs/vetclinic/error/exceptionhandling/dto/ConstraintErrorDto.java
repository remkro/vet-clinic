package pl.kurs.vetclinic.error.exceptionhandling.dto;

public class ConstraintErrorDto {

    private String errorMessage;

    public ConstraintErrorDto(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
