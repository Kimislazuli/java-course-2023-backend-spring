package edu.java.scrapper.controller;

import edu.java.models.dto.response.ApiErrorResponse;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ScrapperExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> exception(
        Exception ex
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new ApiErrorResponse(
                "Внутренняя ошиька сервера",
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toArray(String[]::new)
            )
        );
    }

    @ExceptionHandler(RepeatedRegistrationException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidParameters(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(
            "Повторная регистрация",
            HttpStatus.CONFLICT.toString(),
            ex.getClass().getName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toArray(String[]::new)
        ));
    }
}
