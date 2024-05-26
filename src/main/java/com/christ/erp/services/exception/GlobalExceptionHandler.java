package com.christ.erp.services.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FailedValidationResponse> generalExceptionHandler(Exception e) {
        FailedValidationResponse response = new FailedValidationResponse();
        e.printStackTrace();
        logger.error("Exception handler", e);
        response.setSuccess(false);
        StackTraceElement frame = e.getStackTrace()[0];
        //System.out.println("fileName---"+frame.getFileName()+"Line number---"+frame.getLineNumber()+"  method name--"+frame.getMethodName()+" " +" message---"+e.getMessage()+"  tostring---"+e.toString());
        //response.setErrorMsg("fileName---"+frame.getFileName()+"Line number---"+frame.getLineNumber()+"  method name--"+frame.getMethodName()+" " +" message---"+e.getMessage()+"  tostring---"+e.toString());
        response.setErrorMsg("Sorry, Exception occured..");
     //   response.setErrorMsg(getStackTrace(e));
        return ResponseEntity.badRequest().body(response);
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<FailedValidationResponse> notFoundException(NotFoundException e) {
        FailedValidationResponse response = new FailedValidationResponse();
        response.setSuccess(false);
        response.setErrorMsg(e.getInputMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<FailedValidationResponse> duplicateException(DuplicateException e) {
        FailedValidationResponse response = new FailedValidationResponse();
        response.setSuccess(false);
        response.setErrorMsg(e.getInputMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<FailedValidationResponse> generalException(GeneralException e) {
        FailedValidationResponse response = new FailedValidationResponse();
        response.setSuccess(false);
        response.setErrorMsg(e.getInputMessage());
        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<HttpStatus> expiredJwtException() {
        //FailedValidationResponse response = new FailedValidationResponse();
        //response.setSuccess(false);
        //response.setErrorMsg(e.getInputMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }
}
