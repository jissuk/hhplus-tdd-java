package io.hhplus.tdd.point.Service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.exception.HistoryNotFoundException;
import io.hhplus.tdd.point.exception.PointNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class PointService {


    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;


    /* key 값으로는 모두 동일하게 UserId가 들어간다.*/
    private final Map<Long, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final Map<Long, AtomicLong> userPoints = new ConcurrentHashMap<>();


    // 포인트 조회
    public UserPoint selectById(long id) {

        return findUserPointOrThrow(id);
    }

    // 포인트 충전/사용 내역 조회
    public List<PointHistory> selectAllByUserId(long id) {
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);

        if(pointHistories == null || pointHistories.isEmpty()) {
            throw new HistoryNotFoundException("포인트 내역이 존재하지 않습니다.");
        }

        return pointHistories;
    }

    // 포인트 충전
    public UserPoint chargePoint(long id, long amount){

        // userId 별로 고유한 락을 공유하게 된다.
        ReentrantLock lock = locks.computeIfAbsent(id, userId -> new ReentrantLock());

        // lock을 통해 고유한 락을 공유하는 스레드들끼리 블로킹(대기)된다.
        lock.lock();
        try {
            UserPoint userPoint = findUserPointOrThrow(id);
            userPoint.charge(amount);

            userPointTable.insertOrUpdate(id, userPoint.getPoint());

            UserPoint afterPoint = userPointTable.selectById(id);

            pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
            return afterPoint;
        } finally {
            lock.unlock();
        }
    }

    // 포인트 사용
    public UserPoint usePoint(long id, long amount){
        ReentrantLock lock = locks.computeIfAbsent(id, userId -> new ReentrantLock());

        lock.lock();
        try {
            UserPoint userPoint = findUserPointOrThrow(id);
            userPoint.use(amount);

            userPointTable.insertOrUpdate(id, userPoint.getPoint());

            UserPoint afterPoint = userPointTable.selectById(id);

            pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());
            return afterPoint;

        } finally {
            lock.unlock();
        }
    }

    private UserPoint findUserPointOrThrow(long id) {
        UserPoint userPoint = userPointTable.selectById(id);
        if (userPoint == null) {
            throw new PointNotFoundException("포인트 조회에 실패하였습니다.");
        }
        return userPoint;
    }
}

