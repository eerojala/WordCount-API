package com.eerojala.wordcount.api.web;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;

@RestControllerAdvice
public class WordCountExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        return createBadRequestResponse(exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> createBadRequestResponse(String errorMsg) {
        return createResponse(errorMsg, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> createResponse(String errorMsg, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(new ErrorResponse(errorMsg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
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
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException exception) {
        return createBadRequestResponse(exception.getMessage());
    }

    private record ErrorResponse(String error) {}
}
