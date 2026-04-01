package team9.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import team9.demo.RestDocsTest;
import team9.demo.controller.ai.Aicontroller;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.facade.ai.AiFacade;
import team9.demo.model.ai.analysis.ChatResponse;
import team9.demo.model.ai.analysis.Choice;
import team9.demo.model.ai.analysis.TextMessage;
import team9.demo.model.media.FileData;
import team9.demo.model.user.UserId;
import team9.demo.service.ai.AiService;
import team9.demo.util.handler.GlobalExceptionHandler;
import team9.demo.util.security.UserArgumentResolver;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static team9.demo.RestDocsUtils.*;

@ActiveProfiles("test")
public class AiControllerTest extends RestDocsTest {

    private AiService aiService;
    private AiFacade aiFacade;
    private MockMvc standaloneMockMvc;

    @BeforeEach
    void setUpController(RestDocumentationContextProvider restDocumentation) {
        aiService = mock(AiService.class);
        aiFacade = mock(AiFacade.class);
        Aicontroller controller = new Aicontroller(aiService, aiFacade);

        UserId userId = UserId.of("testUserId");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null)
        );

        ObjectMapper mapper = new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        standaloneMockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new UserArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("이미지 분석 요청 - 성공")
    void imageAnalysis() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "room.jpg", MediaType.IMAGE_JPEG_VALUE, "image-data".getBytes()
        );

        ChatResponse chatResponse = new ChatResponse(
                List.of(new Choice(0, new TextMessage("assistant", "방이 깨끗합니다.")))
        );
        when(aiFacade.requestImageAnalysis(any(FileData.class), eq("방 상태를 분석해주세요"), any(UserId.class)))
                .thenReturn(chatResponse);

        standaloneMockMvc.perform(multipart("/ai/image/analysis")
                        .file(image)
                        .param("requestText", "방 상태를 분석해주세요")
                        .header("Authorization", "Bearer testToken")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.choices[0].message.content").value("방이 깨끗합니다."))
                .andDo(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestParts(
                                partWithName("image").description("분석할 방 이미지 파일")
                        ),
                        queryParameters(
                                parameterWithName("requestText").description("AI에게 전달할 분석 요청 텍스트")
                        ),
                        responseFields(
                                fieldWithPath("choices[]").description("AI 응답 목록"),
                                fieldWithPath("choices[].index").description("응답 인덱스"),
                                fieldWithPath("choices[].message.role").description("메시지 역할 (assistant)"),
                                fieldWithPath("choices[].message.content").description("AI 분석 결과 텍스트")
                        )
                ));

        verify(aiFacade, times(1)).requestImageAnalysis(any(FileData.class), any(), any(UserId.class));
    }

    @Test
    @DisplayName("미션 이미지 비교 검증 - 성공")
    void verifyMissionByImage() throws Exception {
        MockMultipartFile beforeImage = new MockMultipartFile(
                "beforeImage", "before.jpg", MediaType.IMAGE_JPEG_VALUE, "before-data".getBytes()
        );
        MockMultipartFile afterImage = new MockMultipartFile(
                "afterImage", "after.jpg", MediaType.IMAGE_JPEG_VALUE, "after-data".getBytes()
        );

        when(aiService.verifyTodayMissionByImageWithContext(
                eq("mission123"), any(FileData.class), any(FileData.class), any(UserId.class)
        )).thenReturn("미션 완료! 방이 깨끗하게 정리되었습니다.");

        standaloneMockMvc.perform(multipart("/ai/image/mission-verify/{todayMissionId}", "mission123")
                        .file(beforeImage)
                        .file(afterImage)
                        .header("Authorization", "Bearer testToken")
                        .accept(MediaType.TEXT_PLAIN)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("미션 완료! 방이 깨끗하게 정리되었습니다."))
                .andDo(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestParts(
                                partWithName("beforeImage").description("미션 수행 전 이미지"),
                                partWithName("afterImage").description("미션 수행 후 이미지")
                        ),
                        pathParameters(
                                parameterWithName("todayMissionId").description("검증할 오늘의 미션 ID")
                        )
                ));

        verify(aiService, times(1)).verifyTodayMissionByImageWithContext(
                eq("mission123"), any(FileData.class), any(FileData.class), any(UserId.class)
        );
    }

    @Test
    @DisplayName("방 이미지 정리 생성 - 성공")
    void generateCleanedRoomImage() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "messy-room.jpg", MediaType.IMAGE_JPEG_VALUE, "image-data".getBytes()
        );

        when(aiFacade.generateCleanedRoomImageWithLama(any(FileData.class), any(UserId.class)))
                .thenReturn("https://s3.example.com/cleaned-room.jpg");
        when(aiFacade.requestImageAnalysisText(any(), any(UserId.class)))
                .thenReturn("방이 깔끔하게 정리되었습니다. 침대 정리와 바닥 청소가 완료되었습니다.");

        standaloneMockMvc.perform(multipart("/ai/image/generate")
                        .file(image)
                        .header("Authorization", "Bearer testToken")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.choices[0].message.content").exists())
                .andDo(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestParts(
                                partWithName("image").description("정리할 방 이미지 파일")
                        ),
                        responseFields(
                                fieldWithPath("choices[]").description("AI 응답 목록"),
                                fieldWithPath("choices[].index").description("응답 인덱스"),
                                fieldWithPath("choices[].message.role").description("메시지 역할 (assistant)"),
                                fieldWithPath("choices[].message.content").description("정리된 이미지 URL + AI 분석 텍스트")
                        )
                ));

        verify(aiFacade, times(1)).generateCleanedRoomImageWithLama(any(FileData.class), any(UserId.class));
        verify(aiFacade, times(1)).requestImageAnalysisText(any(), any(UserId.class));
    }
}
