package io.hhplus.tdd;

import io.hhplus.tdd.point.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    // 포인트 조회 실패
    @ExceptionHandler(PointNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePointNotFound(PointNotFoundException e){

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("PointNotFound", e.getMessage()));
    }

    // 포인트 내역 조회 실패
    @ExceptionHandler(HistoryNotFoundException.class)
    public ResponseEntity<String> handleHistoryNotFound(HistoryNotFoundException e){
        return ResponseEntity
                .notFound().build();
    }

    // 포인트 잔고 부족
    @ExceptionHandler(InsufficientPointBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientPointBalance(InsufficientPointBalanceException e){

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("InsufficientPointBalance", e.getMessage()));
    }


    // 충전하려는 금액이 0원 이하
    @ExceptionHandler(InvalidPointAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPointAmount(InvalidPointAmountException e){

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("InvalidPointAmount", e.getMessage()));
    }

    // 충전하려는 금액이 한도를 초과
    @ExceptionHandler(PointLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handlePointLimitExceeded(PointLimitExceededException e){

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("PointLimitExceeded", e.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }
}
