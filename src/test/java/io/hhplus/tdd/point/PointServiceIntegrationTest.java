package io.hhplus.tdd.point;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;


/* 서비스 계층(통합 테스트)
 * 책임 : 외부 의존성(DB)이 강한 비지니스 로직 처리 (+트랜잭션)
 * */
@SpringBootTest
public class PointServiceIntegrationTest {

    @Autowired
    private UserPointTable userPointTable;

    @Test
    void 포인트_충전로직_테스트() throws Exception {

        // given
        long id = 1L;
        long amount = 3000L;

        // when
        UserPoint userPoint = userPointTable.selectById(id);
        userPoint.charge(amount);
        UserPoint resultPoint = userPointTable.insertOrUpdate(id, userPoint.getPoint());

        // then
        assertThat(resultPoint).isNotNull();
        assertThat(resultPoint.getId()).isEqualTo(id);
        assertThat(resultPoint.getPoint()).isEqualTo(userPoint.getPoint());
    }

    @Test
    void 포인트_사용로직_테스트(){

        // given
        long defalutAmount = 10000L;
        long id = 1L;
        long amount = 3000L;
        userPointTable.insertOrUpdate(id, defalutAmount);

        // when
        UserPoint userPoint = userPointTable.selectById(id);
        userPoint.use(amount);
        UserPoint resultPoint = userPointTable.insertOrUpdate(id, userPoint.getPoint());

        // then
        assertThat(resultPoint).isNotNull();
        assertThat(resultPoint.getId()).isEqualTo(id);
        assertThat(resultPoint.getPoint()).isEqualTo(userPoint.getPoint());
    }
}
