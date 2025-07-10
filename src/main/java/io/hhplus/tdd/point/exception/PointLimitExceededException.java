package io.hhplus.tdd.point.exception;

public class PointLimitExceededException extends RuntimeException {
    //  충전으로 최대 한도 초과 예외
    public PointLimitExceededException(String message) {
        super(message);
    }
}