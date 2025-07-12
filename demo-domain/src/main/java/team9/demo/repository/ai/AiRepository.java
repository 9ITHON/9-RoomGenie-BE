package team9.demo.repository.ai;

import team9.demo.model.ai.analysis.ChatResponse;
import team9.demo.model.ai.mask.Box;
import team9.demo.model.user.UserId;

import java.io.IOException;
import java.util.List;

public interface AiRepository {
    ChatResponse requestImageAnalysis(String imageUrl, String requestText);
    ChatResponse requestCompareAnalysis(String beforeUrl, String afterUrl, String requestText);
    // GPT Vision을 통한 어질러진 영역 분석
    List<Box> detectClutterBoxes(byte[] imageBytes);
    String editImageWithLama(byte[] imageBytes, byte[] maskBytes, UserId userId);
}