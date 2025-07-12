//package team9.demo.controller;
//
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.restassured.builder.RequestSpecBuilder;
//import io.restassured.http.ContentType;
//import io.restassured.specification.RequestSpecification;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.restdocs.RestDocumentationContextProvider;
//import org.springframework.restdocs.payload.ResponseFieldsSnippet;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import team9.demo.RestDocsTest;
//import team9.demo.controller.mission.MissionController;
//import team9.demo.dto.request.mission.TodayMissionAcceptRequest;
//import team9.demo.dto.request.mission.TodayMissionRequest;
//import team9.demo.error.ConflictException;
//import team9.demo.error.ErrorCode;
//import team9.demo.error.NotFoundException;
//import team9.demo.model.mission.MissionStatus;
//import team9.demo.model.mission.TodayMissionInfo;
//import team9.demo.model.user.UserId;
//import team9.demo.service.mission.MissionService;
//import team9.demo.util.handler.GlobalExceptionHandler;
//import team9.demo.util.security.UserArgumentResolver;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//import static org.hamcrest.Matchers.equalTo;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.BDDMockito.then;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
//import static org.springframework.restdocs.payload.PayloadDocumentation.*;
//
//import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
//import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static team9.demo.RestDocsUtils.*;
//
//@ActiveProfiles("test")
//class MissionControllerTest extends RestDocsTest {
//
//    private MissionService missionService;
//    private MockMvc mockMvc;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//
//
//    @BeforeEach
//    void setUp(RestDocumentationContextProvider restDocumentation) {
//        // 1. Mock 서비스 생성
//        missionService = mock(MissionService.class);
//
//        // 2. 컨트롤러 인스턴스 생성
//        MissionController missionController = new MissionController(missionService);
//
//        // 3. 인증 객체 설정
//        UserId userId = UserId.of("testUserId");
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // 4. MockMvc 설정 (RestDocs 연동)
//        mockMvc = MockMvcBuilders
//                .standaloneSetup(missionController)
//                .setControllerAdvice(new GlobalExceptionHandler())
//                .setCustomArgumentResolvers(new UserArgumentResolver())
//                .apply(documentationConfiguration(restDocumentation))  // ✅ 정확한 위치
//                .build();
//    }
//
//    @Test
//    @DisplayName("오늘의 미션 추천 - 성공")
//    void recommendTodayMission() throws Exception {
//        // given
//        String recommendedMission = "책상 정리하기";
//        when(missionService.recommendOneMission(any())).thenReturn(recommendedMission);
//
//        // when + then
//        mockMvc.perform(
//                        get("/api/today-mission/recommend")
//                                .header("Authorization", "Bearer testToken")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .accept(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.mission").value(recommendedMission))  // ✅ JSON 검증
//                .andDo(document(
//                        "{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        responseFields(  // ✅ JSON 필드 설명 추가
//                                fieldWithPath("mission").description("추천된 오늘의 미션 내용")
//                        ),
//                        requestAccessTokenFields()
//                ));
//    }
//
//    @Test
//    @DisplayName("오늘의 미션 추천 - 실패 (미션 없음)")
//    void recommendTodayMission_fail_when_no_mission_available() throws Exception {
//        // given
//        when(missionService.recommendOneMission(any()))
//                .thenThrow(new NotFoundException(ErrorCode.MISSION_NOT_FOUND));
//
//        // when + then
//        mockMvc.perform(
//                        get("/api/today-mission/recommend")
//                                .header("Authorization", "Bearer testToken")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .accept(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404))
//                .andExpect(jsonPath("$.data.errorCode").value(ErrorCode.MISSION_NOT_FOUND.getCode()))
//                .andExpect(jsonPath("$.data.message").value("미션이 존재하지 않습니다."))
//                .andDo(document(
//                        "{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        requestAccessTokenFields(),
//                        responseErrorFields(
//                                HttpStatus.NOT_FOUND,
//                                ErrorCode.MISSION_NOT_FOUND,
//                                "미션이 존재하지 않는 경우 - 등록된 미션이 하나도 없을 때"
//                        )
//                ));
//    }
////    @Test
////    @DisplayName("오늘의 미션 직접 생성 - 성공")
////    void makeCustomTodayMission_Success() throws Exception {
////        TodayMissionRequest request = new TodayMissionRequest("책상 정리하기");
////        String json = objectMapper.writeValueAsString(request);
////
////        mockMvc.perform(post("/api/today-mission/")
////                        .header("Authorization", "Bearer testToken")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .accept(MediaType.APPLICATION_JSON)
////                        .content(json)
////                )
////                .andExpect(status().isOk())
////                .andDo(document(
////                        "{class-name}/{method-name}",
////                        requestPreprocessor(),
////                        requestFields(
////                                fieldWithPath("mission").description("사용자가 직접 입력한 오늘의 미션 내용")
////                        )
//////                        requestAccessTokenFields()
////                ));
////
////        verify(missionService).makeCustomTodayMission(any(UserId.class), eq("책상 정리하기"));
////    }
////
////    @Test
////    @DisplayName("오늘의 미션 직접 생성 - 실패 (미션 초과)")
////    void makeCustomTodayMission_Fail_ExceedLimit() throws Exception {
////        // given
////        TodayMissionRequest request = new TodayMissionRequest("청소기 돌리기");
////        String json = objectMapper.writeValueAsString(request);
////
////        doThrow(new ConflictException(ErrorCode.TODAY_MISSION_LIMIT_EXCEEDED))
////                .when(missionService).makeCustomTodayMission(any(UserId.class), eq("청소기 돌리기"));
////
////        // ✅ 단일 perform, 단일 document
////        mockMvc.perform(
////                        post("/api/today-mission/")
////                                .header("Authorization", "Bearer testToken")
////                                .contentType(MediaType.APPLICATION_JSON)
////                                .accept(MediaType.APPLICATION_JSON)
////                                .characterEncoding("UTF-8")
////                                .content(json)
////                )
////                .andExpect(status().isConflict())
////                .andDo(document(
////                        "{class-name}/{method-name}",
////                        requestPreprocessor(),
////                        responsePreprocessor(),
////                        requestFields(
////                                fieldWithPath("mission").description("사용자가 입력한 오늘의 미션")
////                        ),
////                        requestAccessTokenFields(),
////                        responseErrorFields(
////                                HttpStatus.CONFLICT,
////                                ErrorCode.TODAY_MISSION_LIMIT_EXCEEDED,
////                                "오늘의 미션 개수가 3개를 초과한 경우"
////                        )
////                ));
////    }
//    //뻥 뻥 터짐
//
//    @Test
//    @DisplayName("추천 미션 수락 - 성공")
//    void acceptRecommendedTodayMission() throws Exception {
//        // given
//        TodayMissionAcceptRequest request = new TodayMissionAcceptRequest("mission123");
//        String json = objectMapper.writeValueAsString(request);
//
//        // missionService.acceptRecommendedTodayMission 은 void 메서드이므로 doNothing() 생략 가능 (기본 동작)
//
//        mockMvc.perform(
//                        post("/api/today-mission/accept")
//                                .header("Authorization", "Bearer testToken")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .accept(MediaType.APPLICATION_JSON)
//                                .content(json)
//                )
//                .andExpect(status().isOk())
//                .andDo(document(
//                        "{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        requestFields(
//                                fieldWithPath("missionId").description("수락할 추천 미션 ID")
//                        ),
//                        requestAccessTokenFields()
//                ));
//
//        // verify 호출 여부 (옵션)
//        verify(missionService).acceptRecommendedTodayMission(any(UserId.class), eq("mission123"));
//    }
//
//
//    @Test
//    @DisplayName("오늘의 미션 목록 조회")
//    void getTodayMissions_ArrayDateCheck() throws Exception {
//        LocalDateTime now = LocalDateTime.of(2025, 7, 11, 21, 28, 42);
//        TodayMissionInfo missionInfo = TodayMissionInfo.of(
//                "mission123",
//                "책상 정리하기",
//                now,
//                MissionStatus.ONGOING
//        );
//
//        when(missionService.getTodayMissions(any(UserId.class)))
//                .thenReturn(List.of(missionInfo));
//
//        mockMvc.perform(
//                        get("/api/today-mission/")
//                                .header("Authorization", "Bearer testToken")
//                                .accept(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].missionId").value("mission123"))
//                .andExpect(jsonPath("$[0].content").value("책상 정리하기"))
//                .andExpect(jsonPath("$[0].targetDate[0]").value(2025))
//                .andExpect(jsonPath("$[0].targetDate[1]").value(7))
//                .andExpect(jsonPath("$[0].targetDate[2]").value(11))
//                .andExpect(jsonPath("$[0].targetDate[3]").value(21))
//                .andExpect(jsonPath("$[0].targetDate[4]").value(28))
//                .andExpect(jsonPath("$[0].targetDate[5]").value(42))
//                .andExpect(jsonPath("$[0].status").value("ONGOING"))
//                .andDo(document(
//                        "{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        responseFields(
//                                fieldWithPath("[].missionId").description("오늘의 미션 ID"),
//                                fieldWithPath("[].content").description("오늘의 미션 내용"),
//                                fieldWithPath("[].targetDate").description("목표 날짜 및 시간 - 배열 형태 [년,월,일,시,분,초,나노초]"),
//                                fieldWithPath("[].status").description("미션 상태 (예: ONGOING, COMPLETED)")
//                                ),
//                        requestAccessTokenFields()
//                ));
//    }
//
//    @Test
//    @DisplayName("오늘의 미션 단건 조회")
//    void getTodayMission() throws Exception {
//        // given
//        String todayMissionId = "mission123";
//        LocalDateTime now = LocalDateTime.of(2025, 7, 11, 21, 28, 42);
//        TodayMissionInfo missionInfo = TodayMissionInfo.of(
//                todayMissionId,
//                "책상 정리하기",
//                now,
//                MissionStatus.ONGOING
//        );
//
//        when(missionService.getTodayMission(any(UserId.class), eq(todayMissionId)))
//                .thenReturn(missionInfo);
//
//        mockMvc.perform(
//                        get("/api/today-mission/{todayMissionId}/", todayMissionId)
//                                .header("Authorization", "Bearer testToken")
//                                .accept(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.missionId").value(todayMissionId))
//                .andExpect(jsonPath("$.content").value("책상 정리하기"))
//                .andExpect(jsonPath("$.targetDate[0]").value(2025))
//                .andExpect(jsonPath("$.targetDate[1]").value(7))
//                .andExpect(jsonPath("$.targetDate[2]").value(11))
//                .andExpect(jsonPath("$.targetDate[3]").value(21))
//                .andExpect(jsonPath("$.targetDate[4]").value(28))
//                .andExpect(jsonPath("$.targetDate[5]").value(42))
//                .andExpect(jsonPath("$.status").value("ONGOING"))
//                .andDo(document(
//                        "{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        pathParameters(
//                                parameterWithName("todayMissionId").description("조회할 오늘의 미션 ID")
//                        ),
//                        responseFields(
//                                fieldWithPath("missionId").description("오늘의 미션 ID"),
//                                fieldWithPath("content").description("오늘의 미션 내용"),
//                                fieldWithPath("targetDate").description("목표 날짜 및 시간 - 배열 형태 [년,월,일,시,분,초,나노초]"),
//                                fieldWithPath("status").description("미션 상태 (예: ONGOING, COMPLETED)")
//                        ),
//                        requestAccessTokenFields()
//                ));
//    }
//
//
//}
//
