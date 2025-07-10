package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.exception.HistoryNotFoundException;
import io.hhplus.tdd.point.exception.PointNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    // 포인트 조회
    public UserPoint selectById(long id) {

        return findUserPointOrThrow(id);
    }

    // 포인트 충전/사용 내역 조회
    public List<PointHistory> selectAllByUserId(long id) {
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);

        if(pointHistories.isEmpty()) {
            throw new HistoryNotFoundException("포인트 내역이 존재하지 않습니다.");
        }

        return pointHistories;
    }

    // 포인트 충전
    public UserPoint chargePoint(long id, long amount){

        // 기존 포인트 조회
        UserPoint userPoint = findUserPointOrThrow(id);

        // 포인트 충전
        userPoint.charge(amount);

        // 포인트 충전(DB)
        userPointTable.insertOrUpdate(id, userPoint.getPoint());

        // 충전 후 포인트 조회
        UserPoint afterPoint = userPointTable.selectById(id);

        // 포인트 내역 추가 및 현재 포인트 리턴
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        return afterPoint;
    }

    // 포인트 사용
    public UserPoint usePoint(long id, long amount){

        // 기존 포인트 조회
        UserPoint userPoint = findUserPointOrThrow(id);

        // 포인트 사용
        userPoint.use(amount);

        // 포인트 사용(DB)
        userPointTable.insertOrUpdate(id, userPoint.getPoint());

        // 충전 후 포인트 조회
        UserPoint afterPoint = userPointTable.selectById(id);

        // 충전 사용이 성공했을 경우 포인트 내역 추가 리턴
        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());
        return afterPoint;
    }

    private UserPoint findUserPointOrThrow(long id) {
        UserPoint userPoint = userPointTable.selectById(id);
        if (userPoint == null) {
            throw new PointNotFoundException("포인트 조회에 실패하였습니다.");
        }
        return userPoint;
    }
}
