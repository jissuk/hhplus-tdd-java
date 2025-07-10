package io.hhplus.tdd.point;

import io.hhplus.tdd.point.exception.InsufficientPointBalanceException;
import io.hhplus.tdd.point.exception.InvalidPointAmountException;
import io.hhplus.tdd.point.exception.PointLimitExceededException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPoint {

    /*
     * 정책 정의
     * 1. 포인트 잔고는 100,000원을 넘어갈 수 없습니다
     *    (잔고 포인트와 충전하려는 포인트의 합이 10만원을 넘어갈 경우 최대 잔고 제한으로 인한 실패처리)
     * 2. 포인트 잔고는 0원 밑으로 내려갈 수 없습니다.
     *    (잔고 포인트보다 결제 포인트가 더 클 경우 잔고 부족으로 인한 실패처리)
     * 3. 0원 이하의 포인트 충전은 불가능합니다.
     * */

    private long id;
    private long point;
    private long time;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public void charge(long point) {

        long resultPoint = this.point + point;
        // 충전하려는 금액이 0원 이하 경우
        if(point <= 0){
            throw new InvalidPointAmountException("충전하려는 금액이 0원 이하입니다.");
        }

        // 충전하려는 금액이나 충전 후 금액이 10만원 초과시 예외
        if((100000 < resultPoint) ||
            (100000 < point)){
            throw new PointLimitExceededException("충전하려는 금액이 한도를 초과하였습니다.");
        }

        this.point = resultPoint;
    }

    public void use(long point){

        long resultPoint = this.point - point;

        // 포인트 잔고 부족 예외
        if(resultPoint < 0){
            throw new InsufficientPointBalanceException("포인트 잔고의 잔액이 부족합니다.");
        }

        this.point = resultPoint;
    }
}
