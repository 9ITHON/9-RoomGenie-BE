package team9.demo.controller;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;
import team9.demo.RestDocsTest;
import team9.demo.controller.mission.MissionController;
import team9.demo.dto.request.mission.MissionCustomRequest;
import team9.demo.dto.request.mission.TodayMissionAcceptRequest;
import team9.demo.error.ConflictException;
import team9.demo.error.ErrorCode;
import team9.demo.error.NotFoundException;
import team9.demo.model.mission.CleaningMission;
import team9.demo.model.mission.MissionStatus;
import team9.demo.model.mission.TodayMissionInfo;
import team9.demo.model.user.UserId;
import team9.demo.service.mission.MissionService;
import team9.demo.util.handler.GlobalExceptionHandler;
import team9.demo.util.security.UserArgumentResolver;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static team9.demo.RestDocsUtils.*;

@ActiveProfiles("test")
public class MissionControllerTest extends RestDocsTest {

    private MissionService missionService;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();



    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        // 1. Mock 서비스 생성
        missionService = mock(MissionService.class);

        // 2. 컨트롤러 인스턴스 생성
        MissionController missionController = new MissionController(missionService);

        // 3. 인증 객체 설정
        UserId userId = UserId.of("testUserId");
        Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. MockMvc 설정 (RestDocs 연동)
        mockMvc = MockMvcBuilders
                .standaloneSetup(missionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new UserArgumentResolver())
                .addFilters(new CharacterEncodingFilter("UTF-8", true)) // ★ 추가
                .apply(documentationConfiguration(restDocumentation))  // ✅ 정확한 위치
                .build();
    }

    @Test
    @DisplayName("오늘의 미션 추천 - 성공")
    void recommendTodayMission() throws Exception {
        // 준비: 미션 서비스의 recommendOneMission 메소드가 반환할 미션 객체 준비
        CleaningMission mission = new CleaningMission("123", "오늘의 미션: 방 청소");

        // 서비스의 동작 정의
        when(missionService.recommendOneMission(any(UserId.class))).thenReturn(mission);

        // 테스트 실행: /api/mission/recommend 요청
        mockMvc.perform(get("/api/today-mission/recommend")
                        .header("Authorization", "Bearer testToken"))  // Authorization 헤더 추가
                .andExpect(status().isOk())  // HTTP 상태 코드 200 (OK)
                .andExpect(jsonPath("$.status").value(200))  // 응답 본문의 status 필드 검증
                .andExpect(jsonPath("$.data.missionId").value(mission.getMissionId()))  // 미션 ID 검증
                .andExpect(jsonPath("$.data.content").value(mission.getContent()))  // 미션 내용 검증
                .andDo(
                        document(
                                "{class-name}/{method-name}",  // 문서화 파일 이름
                                requestPreprocessor(),
                                responsePreprocessor(),
                                responseFields(
                                        fieldWithPath("status").description("상태 코드"),
                                        fieldWithPath("data.missionId").description("오늘의 미션 ID"),
                                        fieldWithPath("data.content").description("오늘의 미션 내용")
                                ),
                                requestAccessTokenFields()
                        )
                );
    }

    @Test
    @DisplayName("오늘의 미션 추천 - 실패 (미션 없음)")
    void recommendTodayMission_fail_when_no_mission_available() throws Exception {
        // given
        when(missionService.recommendOneMission(any()))
                .thenThrow(new NotFoundException(ErrorCode.MISSION_NOT_FOUND));

        // when + then
        mockMvc.perform(
                        get("/api/today-mission/recommend")
                                .header("Authorization", "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data.errorCode").value(ErrorCode.MISSION_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.data.message").value("미션이 존재하지 않습니다."))
                .andDo(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestAccessTokenFields(),
                        responseErrorFields(
                                HttpStatus.NOT_FOUND,
                                ErrorCode.MISSION_NOT_FOUND,
                                "미션이 존재하지 않는 경우 - 등록된 미션이 하나도 없을 때"
                        )
                ));
    }
    @Test
    @DisplayName("오늘의 미션 커스텀 생성 - 성공")
    void makeCustomTodayMission_success() throws Exception {
        // given
        MissionCustomRequest request = new MissionCustomRequest("나만의 미션: 빨래 개기");

        doNothing().when(missionService)
                .makeCustomTodayMission(any(UserId.class), eq(request.getMission()));

        // when + then
        mockMvc.perform(
                        post("/api/today-mission")
                                .header("Authorization", "Bearer testToken")
                                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                                .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andDo(print())

                // 1) 요청 문서화만 먼저
                .andDo(document(
                        "{class-name}/{method-name}-request",
                        // 전처리기 없이 요청 필드 + 인증 헤더만 문서화
                        requestFields(
                                fieldWithPath("mission").description("사용자가 입력한 커스텀 미션 내용")
                        ),
                        requestAccessTokenFields() // = requestHeaders(headerWithName("Authorization") ...)
                ))

                // 2) 응답 문서화만 분리
                .andDo(document(
                        "{class-name}/{method-name}-response",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공 메시지")
                        )
                        // 만약 프로젝트 공통 스니펫을 쓰고 싶으면 아래로 교체 가능
                        // responseSuccessFields()
                ));

        // verify
        verify(missionService, times(1))
                .makeCustomTodayMission(any(UserId.class), eq(request.getMission()));

    }




    @Test
    @DisplayName("오늘의 미션 직접 생성 - 실패 (미션 초과)")
    void makeCustomTodayMission_Fail_ExceedLimit() throws Exception {
        // given
        MissionCustomRequest request = new MissionCustomRequest("청소기 돌리기");

        doThrow(new ConflictException(ErrorCode.TODAY_MISSION_LIMIT_EXCEEDED))
                .when(missionService)
                .makeCustomTodayMission(any(UserId.class), eq("청소기 돌리기"));

        mockMvc.perform(
                        post("/api/today-mission")
                                .header("Authorization", "Bearer testToken")
                                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                                .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.data.errorCode")
                        .value(ErrorCode.TODAY_MISSION_LIMIT_EXCEEDED.getCode()))
                .andDo(print())

                // 1) 요청 문서화만 먼저
                .andDo(document(
                        "{class-name}/{method-name}-request",
                        requestFields(
                                fieldWithPath("mission").description("사용자가 입력한 오늘의 미션")
                        ),
                        requestAccessTokenFields()
                ))

                // 2) 응답 문서화만 분리
                .andDo(document(
                        "{class-name}/{method-name}-response",
                        responseErrorFields(
                                HttpStatus.CONFLICT,
                                ErrorCode.TODAY_MISSION_LIMIT_EXCEEDED,
                                "오늘의 미션 개수가 3개를 초과한 경우"
                        )
                ));

        verify(missionService, times(1))
                .makeCustomTodayMission(any(UserId.class), eq("청소기 돌리기"));
    }
    @Test
    @DisplayName("추천 미션 수락 - 성공")
    void acceptRecommendedTodayMission() throws Exception {
        // given
        TodayMissionAcceptRequest request = new TodayMissionAcceptRequest("mission123");
        String json = objectMapper.writeValueAsString(request);

        // missionService.acceptRecommendedTodayMission 은 void 메서드이므로 doNothing() 생략 가능 (기본 동작)

        mockMvc.perform(
                        post("/api/today-mission/accept")
                                .header("Authorization", "Bearer testToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andDo(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestFields(
                                fieldWithPath("missionId").description("수락할 추천 미션 ID")
                        ),
                        requestAccessTokenFields()
                ));

        // verify 호출 여부 (옵션)
        verify(missionService).acceptRecommendedTodayMission(any(UserId.class), eq("mission123"));
    }


    @Test
    @DisplayName("오늘의 미션 목록 조회")
    void getTodayMissions_ArrayDateCheck() throws Exception {
        // given
        TodayMissionInfo info1 = mock(TodayMissionInfo.class);
        when(info1.getMissionId()).thenReturn("m1");
        when(info1.getContent()).thenReturn("방 청소");
        LocalDateTime t1 = LocalDateTime.of(2025, 8, 18, 10, 30);
        when(info1.getTargetDate()).thenReturn(t1);
        when(info1.getStatus()).thenReturn(MissionStatus.ONGOING);

        TodayMissionInfo info2 = mock(TodayMissionInfo.class);
        when(info2.getMissionId()).thenReturn("m2");
        when(info2.getContent()).thenReturn("빨래 개기");
        LocalDateTime t2 = LocalDateTime.of(2025, 8, 18, 12, 0);
        when(info2.getTargetDate()).thenReturn(t2);
        when(info2.getStatus()).thenReturn(MissionStatus.COMPLETED);

        when(missionService.getTodayMissions(any(UserId.class)))
                .thenReturn(Arrays.asList(info1, info2));

        // 날짜 문자열 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // when + then (단일 perform, 단일 document)
        mockMvc.perform(
                        get("/api/today-mission/") // @GetMapping("/") 기준
                                .header("Authorization", "Bearer testToken")
                                .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].missionId").value("m1"))
                .andExpect(jsonPath("$.data[0].content").value("방 청소"))
                .andExpect(jsonPath("$.data[0].targetDate").value(t1.format(formatter))) // ★ 문자열 포맷 기대
                .andExpect(jsonPath("$.data[0].status").value(MissionStatus.ONGOING.name()))
                .andExpect(jsonPath("$.data[1].missionId").value("m2"))
                .andExpect(jsonPath("$.data[1].content").value("빨래 개기"))
                .andExpect(jsonPath("$.data[1].targetDate").value(t2.format(formatter))) // ★ 문자열 포맷 기대
                .andExpect(jsonPath("$.data[1].status").value(MissionStatus.COMPLETED.name()))
                .andDo(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestAccessTokenFields(),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data[].missionId").description("오늘의 미션 ID"),
                                fieldWithPath("data[].content").description("미션 내용"),
                                fieldWithPath("data[].targetDate").description("목표 날짜/시간(yyyy-MM-dd HH:mm:ss)"), // ★ 설명 변경
                                fieldWithPath("data[].status").description("미션 상태 (ONGOING/COMPLETED 등)")
                        )
                ));

        verify(missionService, times(1)).getTodayMissions(any(UserId.class));
    }

    @Test
    @DisplayName("오늘의 미션 단건 조회")
    void getTodayMission() throws Exception {
        // given
        TodayMissionInfo info = mock(TodayMissionInfo.class);
        when(info.getMissionId()).thenReturn("m1");
        when(info.getContent()).thenReturn("방 청소");
        LocalDateTime target = LocalDateTime.of(2025, 8, 18, 10, 30, 0);
        when(info.getTargetDate()).thenReturn(target);
        when(info.getStatus()).thenReturn(MissionStatus.ONGOING);

        when(missionService.getTodayMission(any(UserId.class), eq("m1")))
                .thenReturn(info);

        // 날짜 문자열 포맷 (컨트롤러 응답이 문자열로 내려오도록 @JsonFormat 등 적용했다고 가정)
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // when + then
        mockMvc.perform(
                        get("/api/today-mission/{todayMissionId}/", "m1")
                                .header("Authorization", "Bearer testToken")
                                .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.missionId").value("m1"))
                .andExpect(jsonPath("$.data.content").value("방 청소"))
                .andExpect(jsonPath("$.data.targetDate").value(target.format(f))) // ← "yyyy-MM-dd HH:mm:ss"
                .andExpect(jsonPath("$.data.status").value(MissionStatus.ONGOING.name()))
                .andDo(
                        document(
                                "{class-name}/{method-name}",
                                // 요청 헤더 + 경로 파라미터 문서화, 응답 필드 문서화
                                requestPreprocessor(),
                                responsePreprocessor(),
                                requestAccessTokenFields(),
                                pathParameters(
                                        parameterWithName("todayMissionId").description("오늘의 미션 ID")
                                ),
                                responseFields(
                                        fieldWithPath("status").description("상태 코드"),
                                        fieldWithPath("data.missionId").description("오늘의 미션 ID"),
                                        fieldWithPath("data.content").description("미션 내용"),
                                        fieldWithPath("data.targetDate").description("목표 날짜/시간(yyyy-MM-dd HH:mm:ss)"),
                                        fieldWithPath("data.status").description("미션 상태")
                                )
                        )
                );

        verify(missionService, times(1)).getTodayMission(any(UserId.class), eq("m1"));

    }
}

