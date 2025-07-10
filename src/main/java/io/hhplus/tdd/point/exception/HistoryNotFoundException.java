package io.hhplus.tdd.point.exception;

public class HistoryNotFoundException extends  RuntimeException {
    public HistoryNotFoundException(String message) {
        super(message);
    }
}
