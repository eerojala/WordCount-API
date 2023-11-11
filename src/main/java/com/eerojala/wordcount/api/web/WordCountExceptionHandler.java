package com.eerojala.wordcount.api.web;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;

@RestControllerAdvice
public class WordCountExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException exception) {
        return createBadRequestResponse(exception.getMessage());
    }

    private ResponseEntity<String> createBadRequestResponse(String errorMsg) {
        return createResponse(errorMsg, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<String> createResponse(String errorMsg, HttpStatus httpStatus) {
        String errorJsonString = "{ \"error\": \"" + errorMsg + "\"}";

        return ResponseEntity.status(httpStatus).body(errorJsonString);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        try {
            var detailMsgArguments = exception.getDetailMessageArguments();
            var validationErrorMsgList = (ArrayList<String>) detailMsgArguments[1];
            String errorMsg = String.join(", ", validationErrorMsgList);

            return createBadRequestResponse(errorMsg);
        } catch (Exception e) {
            String errorMsg = "Error while creating validation error message";

            return createResponse(errorMsg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<String> handleFileUploadException(FileUploadException exception) {
        return createBadRequestResponse(exception.getMessage());
    }
}
