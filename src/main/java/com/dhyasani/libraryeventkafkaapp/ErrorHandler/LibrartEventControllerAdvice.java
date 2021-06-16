package com.dhyasani.libraryeventkafkaapp.ErrorHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class LibrartEventControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleRequestBody(MethodArgumentNotValidException ex){
        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        String errors = fieldErrorList.stream().map(fieldError -> fieldError.getField()+ " - "+fieldError.getDefaultMessage())
                .sorted().collect(Collectors.joining(","));

        log.error("Error message : {}", errors);

        return  new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
