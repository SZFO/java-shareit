package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response> handleException(RuntimeException e) {
        log.info("Ошибка: {}", e.getMessage());

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handleException(NotFoundException e) {
        log.info("Ошибка: {}", e.getMessage());

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<Response> handleException(ValidateException e) {
        log.info("Ошибка: {}", e.getMessage());

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}