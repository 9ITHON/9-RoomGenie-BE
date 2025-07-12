package team9.demo.controller.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team9.demo.dto.response.ChatGPTResponse;
import team9.demo.model.ai.analysis.ChatResponse;
import team9.demo.model.media.FileData;
import team9.demo.model.user.UserId;
import team9.demo.service.ai.AiService;
import team9.demo.util.helper.FileHelper;
import team9.demo.util.security.CurrentUser;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class Aicontroller {

    private final AiService aiService;

    @PostMapping("/image/analysis")
    public ChatGPTResponse imageAnalysis(@RequestParam MultipartFile image, @RequestParam String requestText, @CurrentUser UserId userId) throws IOException {// 수동 변환
        FileData converted = FileHelper.convertMultipartFileToFileData(image);
        ChatResponse result = aiService.requestImageAnalysis(converted, requestText, userId);
        return ChatGPTResponse.of(result.getResultMessage());
    }
//    // request부분 DB에서 랜덤추출
//    @PostMapping("/image/generate")
//    public ChatGPTResponse generateCleanedRoomImage(@RequestParam MultipartFile image, @RequestParam String requestText, @RequestParam(name = "userId") UserId userId) throws IOException {
//        FileData fileData = FileHelper.convertMultipartFileToFileData(image);
//        String resultUrl = aiService.generateCleanedRoomImage(fileData, requestText, userId);
//        return ChatGPTResponse.of(resultUrl); // resultUrl을 사용자에게 반환
//    }// 나중에 로직바꾸기

    @PostMapping("/image/mission-verify/{todayMissionId}")
    public ResponseEntity<String> verifyMissionByImage(
            @PathVariable String todayMissionId,
            @RequestParam MultipartFile beforeImage,
            @RequestParam MultipartFile afterImage,
            @CurrentUser UserId userId
    ) throws IOException {
        String result = aiService.verifyTodayMissionByImageWithContext(
                todayMissionId,
                FileHelper.convertMultipartFileToFileData(beforeImage),
                FileHelper.convertMultipartFileToFileData(afterImage),
                userId
        );
        return ResponseEntity.ok(result);
    }


    @PostMapping("/image/generate")
    public ChatGPTResponse generateCleanedRoomImage(
            @RequestParam MultipartFile image,
            @CurrentUser UserId userId
    ) throws IOException {
        FileData fileData = FileHelper.convertMultipartFileToFileData(image);
        String cleanedImageUrl = aiService.generateCleanedRoomImageWithLama(fileData, userId);

        // GPT 분석 텍스트 생성 예시 (직접 호출하거나 미리 준비)
        String gptAnalysis = aiService.requestImageAnalysisText(cleanedImageUrl, userId);

        return ChatGPTResponse.of(cleanedImageUrl, gptAnalysis);
    }



}
