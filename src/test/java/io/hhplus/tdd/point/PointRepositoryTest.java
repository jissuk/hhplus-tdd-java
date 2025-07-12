package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/* 레파지토리 계층(통합 테스트)
 * 책임 : DB 정상 연동 확인
 * */
@SpringBootTest
public class PointRepositoryTest {

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @Test
    void 포인트_조회_테스트(){

        // given
        long id = 1L;
        long amount = 3000L;
        userPointTable.insertOrUpdate(id, amount);

        // when
        UserPoint userPoint = userPointTable.selectById(id);

        // then
        assertThat(userPoint.getId()).isEqualTo(id);
        assertThat(userPoint.getPoint()).isEqualTo(amount);

    }

    @Test
    void 포인트_충전사용내역_조회_테스트() {
        // given
        long id = 2L;
        long time = System.currentTimeMillis();
        List<PointHistory> pointHistories = List.of(
                new PointHistory(1L, id, 4000, TransactionType.CHARGE, time),
                new PointHistory(2L, id, 3000, TransactionType.USE, time)
        );
        pointHistoryTable.insert(id, pointHistories.get(0).amount(), pointHistories.get(0).type(), time);
        pointHistoryTable.insert(id, pointHistories.get(1).amount(), pointHistories.get(0).type(), time);

        // when
        List<PointHistory> resultList = pointHistoryTable.selectAllByUserId(id);

        // then
        assertThat(resultList)
                .isNotEmpty()
                .hasSize(pointHistories.size());
    }

    @Test
    void 포인트_충전_사용_테스트() throws Exception {
        // given
        long id = 3L;
        long beforePoint = 3000L;
        long afterPoint = 4000L;
        userPointTable.insertOrUpdate(id, beforePoint);

        // when
        UserPoint resultPoint = userPointTable.insertOrUpdate(id, afterPoint);

        // then
        assertThat(resultPoint).isNotNull();
        assertThat(resultPoint.getId()).isEqualTo(id);
        assertThat(resultPoint.getPoint()).isEqualTo(afterPoint);
    }
}

