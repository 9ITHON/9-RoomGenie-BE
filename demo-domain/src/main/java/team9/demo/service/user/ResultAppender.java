package team9.demo.service.user;

import team9.demo.model.user.UserId;


public interface ResultAppender {
    void saveAnalysisResult(UserId userId, String resultText, String imageUrl);
}