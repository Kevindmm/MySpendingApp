package com.kevindmm.spendingapp.exceptions;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.kevindmm.spendingapp.schema.ErrorResponse;
import com.kevindmm.spendingapp.schema.ErrorResponseWrapper;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseWrapper> handleMissingParams(MissingServletRequestParameterException ex) {
        String message = String.format("%s parameter is required", ex.getParameterName());
        List<ErrorResponse> errors = new ArrayList<ErrorResponse>();
        errors.add(new ErrorResponse(message, ex.getParameterName(), "query"));
        ErrorResponseWrapper toReturn = new ErrorResponseWrapper(errors);
        return new ResponseEntity<ErrorResponseWrapper>(toReturn, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseWrapper> handleConstraint(ConstraintViolationException ex) {
        List<ErrorResponse> errors = new ArrayList<ErrorResponse>();
        ConstraintViolation<?> violation = ex.getConstraintViolations().iterator().next();
        String field =  null;
        for(Node node : violation.getPropertyPath()){
            field = node.getName();
        }
        errors.add(new ErrorResponse(violation.getMessage(), field, "query"));
        ErrorResponseWrapper toReturn = new ErrorResponseWrapper(errors);
        return new ResponseEntity<ErrorResponseWrapper>(toReturn, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        Class<?> expectedType = ex.getRequiredType();
        String expectedTypeStr = "Different Type";
        if(expectedType!=null)
            expectedTypeStr = expectedType.getSimpleName();
        String message = String.format("%s should be an %s", paramName, expectedTypeStr);
        ErrorResponse errorResponse = new ErrorResponse(message, paramName, "query");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
