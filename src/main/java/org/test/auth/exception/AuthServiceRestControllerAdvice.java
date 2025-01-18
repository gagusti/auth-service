package org.test.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.test.auth.util.DateFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AuthServiceRestControllerAdvice {

    private ResponseEntity<ExceptionResponse> buildResponse(Map<Integer, String> errors) {
        var responseErrors = errors.entrySet()
                .stream()
                .map(e ->
                        ExceptionResponseBody.builder()
                                .code(e.getKey())
                                .detail(e.getValue())
                                .timestamp(DateFormatter.getDateTimeStamp())
                                .build()
                ).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(ExceptionResponse.builder().error(responseErrors).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<Integer, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = String.format("Field Name: %s - %s", fieldName, error.getDefaultMessage());
            errors.put(AuthErrors.valueOf(fieldName.toUpperCase()).errorCode, errorMessage);
        });
        return buildResponse(errors);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        int errorCode = HttpStatus.BAD_REQUEST.value();
        if (ex instanceof InvalidSingUpException) {
            errorCode = AuthErrors.SIGNUP_FAIL.errorCode;
        }
        else if (ex instanceof InvalidLoginException) {
            errorCode = AuthErrors.LOGIN_FAIL.errorCode;
        }
        return buildResponse( Map.of(errorCode, ex.getMessage()));
    }
}