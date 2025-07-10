package io.hhplus.tdd.point.exception;

public class InsufficientPointBalanceException extends RuntimeException {
    // 사용 금액이 잔고 초과 예외
    public InsufficientPointBalanceException(String message) {
        super(message);
    }
}