package pl.kurs.vetclinic.error.exceptionhandling.dto;

public class FieldValidationErrorDto {

    private String field;
    private String errorMessage;
    private Object rejectedValue;

    public FieldValidationErrorDto(String filed, String errorMessage, Object rejectedValue) {
        this.field = filed;
        this.errorMessage = errorMessage;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(String rejectedValue) {
        this.rejectedValue = rejectedValue;
    }

}
