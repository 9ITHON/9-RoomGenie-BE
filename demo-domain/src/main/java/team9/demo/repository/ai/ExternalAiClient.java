package team9.demo.repository.ai;

import team9.demo.model.ai.analysis.ChatResponse;
import team9.demo.model.ai.mask.Box;
import team9.demo.model.user.UserId;

import java.io.IOException;
import java.util.List;

public interface ExternalAiClient {
    ChatResponse requestImageAnalysis(String imageUrl, String requestText);
    ChatResponse requestCompareAnalysis(String beforeUrl, String afterUrl, String requestText);
    List<Box> detectClutterBoxes(byte[] imageBytes);
    String editImageWithLama(byte[] imageBytes, byte[] maskBytes, String prompt, UserId userId);
}