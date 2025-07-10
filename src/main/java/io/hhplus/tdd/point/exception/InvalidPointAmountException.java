package io.hhplus.tdd.point.exception;

public class InvalidPointAmountException extends RuntimeException {
    // . 충전 금액이 0원 이하 예외
    public InvalidPointAmountException(String message) {
        super(message);
    }
}