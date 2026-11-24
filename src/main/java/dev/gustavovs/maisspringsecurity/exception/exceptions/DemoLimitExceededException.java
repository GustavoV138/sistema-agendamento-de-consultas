package dev.gustavovs.maisspringsecurity.exception.exceptions;

public class DemoLimitExceededException extends RuntimeException {
    public DemoLimitExceededException(String message) {
        super(message);
    }
}
