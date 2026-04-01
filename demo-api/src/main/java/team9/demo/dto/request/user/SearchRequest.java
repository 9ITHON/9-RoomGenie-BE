package team9.demo.dto.request.user;

import team9.demo.model.user.UserInfo;

public record SearchRequest (
        String userName
){
    public static SearchRequest of(UserInfo userInfo) {
        return new SearchRequest(
                userInfo.getUserName()
        );

    }


}
