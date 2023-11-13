package com.eerojala.wordcount.api.exception;

public class FileReadException extends RuntimeException{
    public FileReadException(String message) {
        super(message);
    }
}
