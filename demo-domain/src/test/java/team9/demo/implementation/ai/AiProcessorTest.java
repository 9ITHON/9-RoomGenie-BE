package team9.demo.implementation.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.model.ai.mask.Box;
import team9.demo.model.media.FileData;
import team9.demo.model.user.UserId;
import team9.demo.repository.ai.ExternalAiClient;
import team9.demo.service.user.AiAnalysisGenerator;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link AiProcessor}의 오케스트레이션 동작 검증.
 * <p>
 * PR1(책임 분리)에서 도메인이 {@code ExternalAiClient} 인터페이스에만 의존하도록 만든 효과를
 * 직접 보여주는 테스트 — 실제 GPT/YOLO/LAMA 호출 없이 mock으로 흐름만 검증한다.
 */
@ExtendWith(MockitoExtension.class)
class AiProcessorTest {

    @Mock
    private ExternalAiClient externalAiClient;

    @Mock
    private AiAnalysisGenerator aiAnalysisGenerator;

    @Mock
    private AiPromptGenerator aiPromptGenerator;

    @InjectMocks
    private AiProcessor aiProcessor;

    private final UserId userId = UserId.of("user-1");

    @Test
    @DisplayName("YOLO가 빈 박스 리스트를 반환하면 LAMA를 호출하지 않고 AI_DETECTION_EMPTY를 던진다")
    void cleanImageWithLama_emptyBoxes_throwsDetectionEmpty() {
        FileData image = FileData.of(null, null, "room.jpg", 1024L, new byte[]{1, 2, 3}, 800, 600);
        when(externalAiClient.detectClutterBoxes(any())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> aiProcessor.cleanImageWithLama(image, 800, 600, userId))
                .isInstanceOf(AiException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.AI_DETECTION_EMPTY);

        // LAMA는 절대 호출되지 않아야 함 — 빈 마스크로 LAMA를 때리는 낭비/오류 방지
        verify(externalAiClient, never()).editImageWithLama(any(), any(), anyString(), any());
    }

    @Test
    @DisplayName("YOLO가 박스를 반환하면 마스크를 만들어 LAMA를 호출하고 결과 URL을 반환한다")
    void cleanImageWithLama_normalFlow_returnsCleanedUrl() {
        FileData image = FileData.of(null, null, "room.jpg", 1024L, new byte[]{1, 2, 3}, 200, 200);
        List<Box> boxes = List.of(new Box(50, 50, 60, 60));

        when(externalAiClient.detectClutterBoxes(any())).thenReturn(boxes);
        when(aiPromptGenerator.lamaInpainting()).thenReturn("clean prompt");
        when(externalAiClient.editImageWithLama(any(), any(), eq("clean prompt"), eq(userId)))
                .thenReturn("https://s3/cleaned/result.png");

        String resultUrl = aiProcessor.cleanImageWithLama(image, 200, 200, userId);

        assertThat(resultUrl).isEqualTo("https://s3/cleaned/result.png");
        verify(externalAiClient).detectClutterBoxes(image.getContent());
        verify(externalAiClient).editImageWithLama(any(), any(), eq("clean prompt"), eq(userId));
    }

    @Test
    @DisplayName("이미지 분석 결과는 외부 클라이언트 응답을 그대로 반환하면서 분석 결과를 저장한다")
    void requestImageAnalysis_savesAnalysisAndReturnsResponse() {
        team9.demo.model.ai.analysis.ChatResponse mockResponse = new team9.demo.model.ai.analysis.ChatResponse(
                List.of(new team9.demo.model.ai.analysis.Choice(0,
                        new team9.demo.model.ai.analysis.TextMessage("assistant", "정리 가이드 내용")))
        );
        when(externalAiClient.requestImageAnalysis("https://img/url", "정리법"))
                .thenReturn(mockResponse);

        team9.demo.model.ai.analysis.ChatResponse result =
                aiProcessor.requestImageAnalysis("https://img/url", "정리법", userId);

        assertThat(result.getResultMessage()).isEqualTo("정리 가이드 내용");
        verify(aiAnalysisGenerator).saveAnalysisResult(userId, "정리 가이드 내용", "https://img/url");
    }
}
