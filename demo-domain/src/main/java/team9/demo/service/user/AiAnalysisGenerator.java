package team9.demo.service.user;

import team9.demo.model.user.UserId;


public interface AiAnalysisGenerator {
    void saveAnalysisResult(UserId userId, String resultText, String imageUrl);
}