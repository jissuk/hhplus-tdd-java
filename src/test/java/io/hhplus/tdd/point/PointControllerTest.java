package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/* 컨트롤러 계층(통합 테스트)
* 책임 : 요청과 응답(+예외)의 검증
* */
@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerTest {

    /* controller 계층에서는 요청 및 응답 확인*/
    // SpringBootTest + AutoConfigureMockMvc 로 인해 자동 주입
    @Autowired
    private MockMvc mockMvc;

    // SpringBootTest로 인해 자동 주입
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PointService pointService;


    static final long defalutId = 1l;
    static final long dealutPoint = 3000L;

    @BeforeEach
    public void pointSetup() {
        pointService.chargePoint(defalutId, dealutPoint);
    }

    @Test
    void 포인트_조회_테스트() throws Exception {
        // given
        long id = 1L;
        long amount = 3000L;

        // when
        mockMvc.perform(get("/point/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)) // Mock.perform을 통해 Http 요청을 테스트
                        // then
                        .andExpect(status().isOk())                                              // HttpStatus 검증
                        .andExpect(jsonPath("$.id").value(id))                          // responseBody 필드 검증
                        .andExpect(jsonPath("$.point").value(amount));
    }

    // 로직이 기본 값을 무조건 반환하게 되어있어 테스트 불가능
//    @Test
//    @DisplayName("포인트_조회_잘못된ID_테스트")
//    void 포인트_조회_예외_테스트() throws Exception {
//        // given
//        long id = 99L;
//
//        // when
//        mockMvc.perform(get("/point/{id}",id)
//                        .contentType(MediaType.APPLICATION_JSON))
//                // then
//                .andExpect(status().isNotFound());
//    }

    @Test
    void 포인트_내역_조회_테스트() throws Exception {
        // given
        long id = 1L;
        long useAmount = 2000L;
        pointService.usePoint(id, useAmount);

        // when
        mockMvc.perform(get("/point/{id}/histories",id)
                        .contentType(MediaType.APPLICATION_JSON))
                    // then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].userId").value(id))
                    .andExpect(jsonPath("$[1].amount").value(useAmount))
                    .andExpect(jsonPath("$[1].type").value(TransactionType.USE.name()));
    }

    @Test
    void 포인트_내역_조회_내역존재하지않음_테스트() throws Exception {
        // given
        long id = 2L;

        // when
        mockMvc.perform(get("/point/{id}/histories",id)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    void 포인트_충전_테스트() throws Exception {
        // given
        long id = 1L;
        long chareAmount = 4000L;
        long resultAmount = dealutPoint + chareAmount;

        // when
        mockMvc.perform(patch("/point/{id}/charge",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chareAmount))
                )
                    // then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.point").value(resultAmount));
    }

    @Test
    void 포인트_충전금액_0이하_예외발생_테스트() throws Exception {

        // given
        long id = 1L;
        long chareAmount = 0;

        // when
        mockMvc.perform(patch("/point/{id}/charge",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chareAmount))
                )
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    void 포인트_충전_잔고한도초과_예외발생_테스트() throws Exception {

        // given
        long id = 1L;
        long amount = 110000;

        // when
        mockMvc.perform(patch("/point/{id}/charge",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amount))
                )
                // then
                .andExpect(status().isBadRequest());
    }


    @Test
    void 포인트_사용_테스트() throws Exception {
        // given
        long id = 1L;
        long useAmount = 3000L;
        long resultAmount = dealutPoint - useAmount;

        // when
        mockMvc.perform(patch("/point/{id}/use", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(useAmount))
                )
                    // then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.point").value(resultAmount));
    }

    @Test
    void 포인트_사용_잔액부족_예외발생_테스트() throws Exception {
        // given
        long id = 1L;
        long amount = 4000L;

        // when
        mockMvc.perform(patch("/point/{id}/use",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amount))
                )
                // then
                .andExpect(status().isBadRequest());
    }
}

