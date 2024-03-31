package edu.java.scrapper.exception;

public class RetryException extends RuntimeException {
    public RetryException(String message) {
        super(message);
    }
}
