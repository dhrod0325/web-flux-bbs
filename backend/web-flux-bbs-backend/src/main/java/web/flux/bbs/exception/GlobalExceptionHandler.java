package web.flux.bbs.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> addFieldErrorToMap(fieldError, errors));

        return ResponseEntity.badRequest().body(errors);
    }

    private static void addFieldErrorToMap(FieldError fieldError, Map<String, String> errors) {
        errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
}