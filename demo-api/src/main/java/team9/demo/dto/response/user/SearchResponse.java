package team9.demo.dto.response.user;

import team9.demo.dto.request.user.SearchRequest;
import team9.demo.model.user.UserInfo;

import java.util.List;

public record SearchResponse(
        String userId,
        String userName
) {
    // 도메인 객체에서 변환
    public static SearchResponse of(String userId, String userName) {
        return new SearchResponse(userId, userName);
    }



}