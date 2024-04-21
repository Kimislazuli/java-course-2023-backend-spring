package edu.java.models.dto.backoff_retry;

public class RetryException extends RuntimeException {
    public RetryException(String message) {
        super(message);
    }
}
