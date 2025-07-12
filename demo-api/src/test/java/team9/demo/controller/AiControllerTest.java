//package team9.demo.controller;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.restdocs.RestDocumentationContextProvider;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import team9.demo.RestDocsTest;
//import team9.demo.TestUserArgumentResolver;
//import team9.demo.controller.ai.Aicontroller;
//import team9.demo.controller.mission.MissionController;
//import team9.demo.model.media.FileData;
//import team9.demo.model.user.UserId;
//import team9.demo.service.ai.AiService;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
//import static org.springframework.restdocs.payload.PayloadDocumentation.*;
//import static org.springframework.restdocs.request.RequestDocumentation.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static team9.demo.RestDocsUtils.*;
//
//import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
//import org.springframework.security.test.context.support.WithMockUser;
//import team9.demo.service.mission.MissionService;
//import team9.demo.util.handler.GlobalExceptionHandler;
//import team9.demo.util.helper.FileHelper;
//import team9.demo.util.security.UserArgumentResolver;
//
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//@ActiveProfiles("test")
//public class AiControllerTest extends RestDocsTest {
//
//    private AiService aiService;
//    private MockMvc mockMvc;
//
//
////
////    @BeforeEach
////    void setUp(RestDocumentationContextProvider restDocumentation) {
////        aiService = mock(AiService.class);
////        Aicontroller aiController = new Aicontroller(aiService);
////
////        UserId userId = UserId.of("testUserId");
////        Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null);
////        SecurityContextHolder.getContext().setAuthentication(authentication);
////
////        mockMvc = MockMvcBuilders.standaloneSetup(aiController)
////                .setControllerAdvice(new GlobalExceptionHandler())
////                .setCustomArgumentResolvers(new UserArgumentResolver())
////                .apply(documentationConfiguration(restDocumentation))
////                .build();
////    }
////    @Test
////    @DisplayName("미션 이미지 비교 검증 - 성공 (한글 응답 검증 생략, 문서화만 수행)")
////    void verifyMissionByImage_Success() throws Exception {
////        String todayMissionId = "mission123";
////
////        byte[] imageBytes;
////        try (InputStream is = getClass().getClassLoader().getResourceAsStream("sample.jpg")) {
////            if (is == null) throw new RuntimeException("테스트 리소스 sample.jpg 없음");
////            imageBytes = is.readAllBytes();
////        }
////
////        MockMultipartFile beforeImage = new MockMultipartFile(
////                "beforeImage", "before.jpg", MediaType.IMAGE_JPEG_VALUE, imageBytes
////        );
////
////        MockMultipartFile afterImage = new MockMultipartFile(
////                "afterImage", "after.jpg", MediaType.IMAGE_JPEG_VALUE, imageBytes
////        );
////
////        when(aiService.verifyTodayMissionByImageWithContext(
////                eq(todayMissionId),
////                any(FileData.class),
////                any(FileData.class),
////                any(UserId.class)
////        )).thenReturn("성공입니다, 방이 잘 정리되었습니다.");
////
////        mockMvc.perform(multipart("/ai/image/mission-verify/{todayMissionId}", todayMissionId)
////                        .file(beforeImage)
////                        .file(afterImage)
////                        .header("Authorization", "Bearer testToken")
////                        .accept(MediaType.TEXT_PLAIN)
////                )
////                .andExpect(status().isOk()) // 상태코드만 검증
////                .andDo(document(
////                        "{class-name}/{method-name}",
////                        requestPreprocessor(),
////                        responsePreprocessor(),
////                        requestParts(
////                                partWithName("beforeImage").description("비교 전 이미지 파일"),
////                                partWithName("afterImage").description("비교 후 이미지 파일")
////                        ),
////                        pathParameters(
////                                parameterWithName("todayMissionId").description("오늘의 미션 ID")
////                        ),
////                        requestAccessTokenFields()
////                ));
////
////        verify(aiService).verifyTodayMissionByImageWithContext(
////                eq(todayMissionId),
////                any(FileData.class),
////                any(FileData.class),
////                any(UserId.class)
////        );
////    }
//
////    @Test
////    @DisplayName("방 이미지 정리 요청 - 성공")
////    void generateCleanedRoomImage_Success() throws Exception {
////        byte[] imageBytes;
////        try (InputStream is = getClass().getClassLoader().getResourceAsStream("sample.jpg")) {
////            if (is == null) throw new RuntimeException("테스트 리소스 sample.jpg 없음");
////            imageBytes = is.readAllBytes();
////        }
////
////        MockMultipartFile imageFile = new MockMultipartFile(
////                "image",
////                "sample.jpg",
////                MediaType.IMAGE_JPEG_VALUE,
////                imageBytes
////        );
////
////        String cleanedImageUrl = "https://example.com/cleaned-room.jpg";
////
////        when(aiService.generateCleanedRoomImageWithLama(any(FileData.class), any(UserId.class)))
////                .thenReturn(cleanedImageUrl);
////
////        mockMvc.perform(multipart("/ai/image/generate")
////                        .file(imageFile)
////                        .header("Authorization", "Bearer testToken")
////                        .accept(MediaType.APPLICATION_JSON)
////                )
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.choices[0].message.content").value(cleanedImageUrl))
////                .andDo(document(
////                        "{class-name}/{method-name}",
////                        requestPreprocessor(),
////                        responsePreprocessor(),
////                        requestParts(
////                                partWithName("image").description("정리할 방 이미지 파일")
////                        ),
////                        requestAccessTokenFields(),
////                        responseFields(
////                                fieldWithPath("choices").description("응답 메시지 리스트"),
////                                fieldWithPath("choices[].index").description("메시지 인덱스"),
////                                fieldWithPath("choices[].message.role").description("메시지 역할 (assistant)"),
////                                fieldWithPath("choices[].message.content").description("정리된 방 이미지 URL")
////                        )
////                ));
////
////        verify(aiService).generateCleanedRoomImageWithLama(any(FileData.class), any(UserId.class));
////    }
//
//
//
//}
