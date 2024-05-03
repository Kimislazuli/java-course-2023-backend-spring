package edu.java.bot.exception;

public class RetryException extends RuntimeException {
    public RetryException(String message) {
        super(message);
    }
}
