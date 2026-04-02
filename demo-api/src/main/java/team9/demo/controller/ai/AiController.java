package team9.demo.controller.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team9.demo.dto.response.ChatGPTResponse;
import team9.demo.facade.ai.AiFacade;
import team9.demo.model.ai.analysis.ChatResponse;
import team9.demo.model.media.FileData;
import team9.demo.model.user.UserId;
import team9.demo.util.helper.FileHelper;
import team9.demo.util.security.CurrentUser;

import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiFacade aiFacade;

    @PostMapping("/image/analysis")
    public ChatGPTResponse imageAnalysis(@RequestParam MultipartFile image, @RequestParam String requestText, @CurrentUser UserId userId) {
        FileData converted = convertFile(image);
        ChatResponse result = aiFacade.requestImageAnalysis(converted, requestText, userId);
        return ChatGPTResponse.of(result.getResultMessage());
    }

    @PostMapping("/image/mission-verify/{todayMissionId}")
    public ResponseEntity<String> verifyMissionByImage(
            @PathVariable String todayMissionId,
            @RequestParam MultipartFile beforeImage,
            @RequestParam MultipartFile afterImage,
            @CurrentUser UserId userId
    ) {
        String result = aiFacade.verifyTodayMissionByImage(
                todayMissionId,
                convertFile(beforeImage),
                convertFile(afterImage),
                userId
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping("/image/generate")
    public ChatGPTResponse generateCleanedRoomImage(
            @RequestParam MultipartFile image,
            @CurrentUser UserId userId
    ) {
        FileData fileData = convertFile(image);
        String cleanedImageUrl = aiFacade.generateCleanedRoomImageWithLama(fileData, userId);
        String gptAnalysis = aiFacade.requestImageAnalysisText(cleanedImageUrl, userId);
        return ChatGPTResponse.of(cleanedImageUrl, gptAnalysis);
    }

    private FileData convertFile(MultipartFile file) {
        try {
            return FileHelper.convertMultipartFileToFileData(file);
        } catch (IOException e) {
            throw new AiException(ErrorCode.AI_IMAGE_READ_FAILED);
        }
    }
}
