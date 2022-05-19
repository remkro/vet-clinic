package pl.kurs.vetclinic.error.exceptionhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.kurs.vetclinic.error.exception.NoEntityException;
import pl.kurs.vetclinic.error.exception.WrongIdException;
import pl.kurs.vetclinic.error.exceptionhandling.constrainterrorhandler.ConstraintErrorHandler;
import pl.kurs.vetclinic.error.exceptionhandling.dto.ConstraintErrorDto;
import pl.kurs.vetclinic.error.exceptionhandling.dto.ExceptionErrorDto;
import pl.kurs.vetclinic.error.exceptionhandling.dto.FieldValidationErrorDto;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Map<String, ConstraintErrorHandler> handlerMap = new HashMap<>();

    public GlobalExceptionHandler(Set<ConstraintErrorHandler> errorHandlers) {
        for (ConstraintErrorHandler errorHandler : errorHandlers) {
            handlerMap.put(errorHandler.getConstraintName(), errorHandler);
        }
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<List<FieldValidationErrorDto>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldValidationErrorDto> fieldErrors = e.getFieldErrors()
                .stream()
                .map(fe -> new FieldValidationErrorDto(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(fieldErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<List<ConstraintErrorDto>> handleConstraintViolationException(ConstraintViolationException e) {
        List<ConstraintErrorDto> constraintErrors = new ArrayList<>();
        for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
            String constraintName = constraintViolation.getMessage();
            constraintErrors.add(handlerMap.getOrDefault(constraintName, handlerMap.get("HACKER_CONSTRAINT")).buildDto());
        }
        return new ResponseEntity<>(constraintErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ExceptionErrorDto> handleObjectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e) {
        ExceptionErrorDto response = new ExceptionErrorDto("OPTIMISTIC_LOCK_EXCEPTION_OCCURRED");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({WrongIdException.class})
    public ResponseEntity<ExceptionErrorDto> handleWrongIdException(Exception e) {
        ExceptionErrorDto response = new ExceptionErrorDto(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({NoEntityException.class})
    public ResponseEntity<ExceptionErrorDto> handleSomeException(Exception e) {
        ExceptionErrorDto response = new ExceptionErrorDto(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

}
