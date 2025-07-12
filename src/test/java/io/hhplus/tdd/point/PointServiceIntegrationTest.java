package io.hhplus.tdd.point;
import io.hhplus.tdd.point.Service.PointService;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;


/* 서비스 계층(통합 테스트)
 * 책임 : 외부 의존성(DB)이 강한 비지니스 로직 처리 (+트랜잭션)
 * */
@SpringBootTest
public class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

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
        long id = 1L;
        long amount = 3000L;
        long defalutAmount = 10000L;
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

    @Test
    void 동시에_100번_요청해도_포인트_충전되는지_테스트() throws InterruptedException {
        // given
        long id = 1L;
        long amount = 100L;
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> pointService.chargePoint(id, amount));
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);
        UserPoint resultPoint  = pointService.selectById(id);

        // then
        assertThat(finished).isTrue();
        assertThat(resultPoint.getPoint()).isEqualTo(10000L);
    }

    @Test
    void 동시에_100번_요청해도_포인트_사용되는지_테스트() throws InterruptedException {
        // given
        long id = 1L;
        long amount = 100L;
        long defalutAmount = 20000L;
        userPointTable.insertOrUpdate(id, defalutAmount);

        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> pointService.usePoint(id, amount));
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);
        UserPoint resultPoint  = pointService.selectById(id);

        // then
        assertThat(finished).isTrue();
        assertThat(resultPoint.getPoint()).isEqualTo(10000L);
    }

    @Test
    void 동시에_100번_요청해도_포인트_충전및사용되는지_테스트() throws InterruptedException {
        // given
        long id = 1L;
        long chargeAmount = 100L;          // 100 포인트 50번 충전
        long useAmount = 200L;             // 200 포인트 50번 사용
        long defalutAmount = 30000L;       // 시작 금액
        userPointTable.insertOrUpdate(id, defalutAmount);

        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> pointService.chargePoint(id, chargeAmount));
        }

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> pointService.usePoint(id, useAmount));
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);
        UserPoint resultPoint  = pointService.selectById(id);

        // then
        assertThat(finished).isTrue();
        assertThat(resultPoint.getPoint()).isEqualTo(25000L);
    }

}
