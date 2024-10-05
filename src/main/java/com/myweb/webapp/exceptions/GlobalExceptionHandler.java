package com.myweb.webapp.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class GlobalExceptionHandler{
    
    //handle database connection exception
    @ExceptionHandler(DbConnectionException.class)
    public ResponseEntity<String> handleDbConnectionException(DbConnectionException e) {
        return  ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "nosniff")
                    .header("Connection", "keep-alive")
                    .build();
    }

    //handle no resource found exception
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "nosniff")
                    .build();
    }

    //handle unallowed payload exception        
    @ExceptionHandler(UnallowedPayloadException.class)
    public ResponseEntity<String> handleUnallowedPayloadException(UnallowedPayloadException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "nosniff")
                    .build();   
    }
    
    //handle request method not supported exception     
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "nosniff")
                    .build();
    }

    //handle email exists exception
    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<String> handleEmailExistsException(EmailExistsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "nosniff")
                    .build();
    }

    //handle validation exception
    @ExceptionHandler(ParamException.class)
    public ResponseEntity<String> handleParamException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "nosniff")
                    .build();
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(HandleBadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(HandleBadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff")
                .build();
    }

    @ExceptionHandler(DatabaseAccessException.class)
    public ResponseEntity<String> handleDatabaseAccessException(DatabaseAccessException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }


}
