package com.auth.template.exception;

import com.auth.template.payload.ErrorDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandling extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String,Object> errors = new LinkedHashMap<>();
        errors.put("timestamp",System.currentTimeMillis());
        errors.put("status",status.value());
        errors.put("uri",request.getDescription(false));
        ex.getBindingResult().getAllErrors().forEach(x->{
            String fieldName = ((FieldError)x).getField();
            String message = x.getDefaultMessage();
            errors.put(fieldName,message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorDetails> handleAuthApiException(AuthException exception, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception exception, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
