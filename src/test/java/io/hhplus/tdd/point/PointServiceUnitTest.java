package io.hhplus.tdd.point;


import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

/* 서비스 계층(단위 테스트)
 * 책임 : 격리성을 가진 조건 분기, 계산, 예외처리 검증
 * */
@ExtendWith(MockitoExtension.class)
public class PointServiceUnitTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;


    @Test
    void 포인트조회_존재하지않는ID_Null반환(){

        //given
        long id = 99L;
        when(userPointTable.selectById(id)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> pointService.selectById(id))
                .isInstanceOf(PointNotFoundException.class)
                .hasMessage("포인트 조회에 실패하였습니다.");
    }


    @Test
    void 포인트_충전_사용내역_존재하지않는ID_Null반환() {
        // given
        long id = 99L;
        when(pointHistoryTable.selectAllByUserId(id)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> pointService.selectAllByUserId(id))
                .isInstanceOf(HistoryNotFoundException.class)
                .hasMessage("포인트 내역 조회에 실패하였습니다.");
    }
    
    @Test
    void 포인트_충전금액_0이하_예외발생(){

        // given
        long id = 1L;
        long amount = -1000;
        UserPoint userPoint = new UserPoint(id, 0L, System.currentTimeMillis());
        when(userPointTable.selectById(id)).thenReturn(userPoint);

        // when & then
        assertThatThrownBy(() -> pointService.chargePoint(id, amount))
                .isInstanceOf(InvalidPointAmountException.class)
                .hasMessage("충전하려는 금액이 0원 이하입니다.");
    }

    @Test
    void 포인트_충전_잔고한도초과_예외발생(){

        // given
        long id = 1L;
        long amount = 110000;
        UserPoint userPoint = new UserPoint(id, 0L, System.currentTimeMillis());
        when(userPointTable.selectById(id)).thenReturn(userPoint);

        // when & then
        assertThatThrownBy(() -> pointService.chargePoint(
                                                    id,
                                                    amount
                                                    )
                            )
                            .isInstanceOf(PointLimitExceededException.class)
                            .hasMessage("충전하려는 금액이 한도를 초과하였습니다.");
    }

    @Test
    void 포인트_사용_잔액부족_예외발생(){

        // given
        long id = 1L;
        long amount = 110000;
        UserPoint userPoint = new UserPoint(id, 0L, System.currentTimeMillis());
        when(userPointTable.selectById(id)).thenReturn(userPoint);

        // when & then
        assertThatThrownBy(() -> pointService.usePoint(id, amount))
                .isInstanceOf(InsufficientPointBalanceException.class)
                .hasMessage("포인트 잔고의 잔액이 부족합니다.");
    }
}
