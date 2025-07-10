package io.hhplus.tdd.point;

import io.hhplus.tdd.point.exception.InsufficientPointBalanceException;
import io.hhplus.tdd.point.exception.InvalidPointAmountException;
import io.hhplus.tdd.point.exception.PointLimitExceededException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/* 도메인 계층(단위 테스트)
 * 자체 비즈니스 규칙 검증
 */
public class UserPointTest {

    @Test
    void 포인트_충전_케이스(){
        // given
        UserPoint userPoint = new UserPoint(1L, 3000L, System.currentTimeMillis());

        // when
        userPoint.charge(500L);

        // then
        assertThat(userPoint.getPoint()).isEqualTo(3500L);
    }

    @Test
    void 포인트_사용_케이스(){
        // given
        UserPoint userPoint = new UserPoint(1L, 3000L, System.currentTimeMillis());

        // when
        userPoint.use(500L);

        // then
        assertThat(userPoint.getPoint()).isEqualTo(2500L);
    }

    @Test
    void 포인트_충전금액_10만원이상_예외_케이스(){
        // given
        long chargeAmount = 110000L;
        UserPoint userPoint = new UserPoint(1L, 3000L, System.currentTimeMillis());

        // when & then
        assertThatThrownBy(() -> userPoint.charge(chargeAmount))
                .isInstanceOf(PointLimitExceededException.class)
                .hasMessage("충전하려는 금액이 한도를 초과하였습니다.");
    }

    @Test
    void 포인트_충전금액_0원이하_예외_케이스(){

        // given
        UserPoint userPoint = new UserPoint(1L, 3000L, System.currentTimeMillis());
        long chargeAmount = 0L;

        // when & given
        assertThatThrownBy(() -> userPoint.charge(chargeAmount))
                .isInstanceOf(InvalidPointAmountException.class)
                .hasMessage("충전하려는 금액이 0원 이하입니다.");
    }

    @Test
    void 포인트_사용금액_잔고초과_예외_케이스(){
        // given
        UserPoint userPoint = new UserPoint(1L, 3000L, System.currentTimeMillis());
        long useAmount = 100000L;

        // when & then
        assertThatThrownBy(() -> userPoint.use(useAmount))
                .isInstanceOf(InsufficientPointBalanceException.class)
                .hasMessage("포인트 잔고의 잔액이 부족합니다.");
    }
}
