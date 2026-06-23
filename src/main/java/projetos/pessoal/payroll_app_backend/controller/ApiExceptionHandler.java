package projetos.pessoal.payroll_app_backend.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleNotFoundOrBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApiError(exception.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleInvalidState(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiError(exception.getMessage(), LocalDateTime.now()));
    }

    public record ApiError(String message, LocalDateTime timestamp) {
    }
}
