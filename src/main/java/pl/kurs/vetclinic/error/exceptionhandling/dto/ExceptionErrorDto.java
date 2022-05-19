package pl.kurs.vetclinic.error.exceptionhandling.dto;

public class ExceptionErrorDto {

    private String errorMessage;

    public ExceptionErrorDto(String errorMessages) {
        this.errorMessage = errorMessages;
    }

    public String getErrorMessages() {
        return errorMessage;
    }

    public void setErrorMessages(String errorMessages) {
        this.errorMessage = errorMessages;
    }

}
