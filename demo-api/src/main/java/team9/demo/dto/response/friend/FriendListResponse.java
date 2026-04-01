package team9.demo.dto.response.friend;

import team9.demo.model.friend.Friend;

import java.util.ArrayList;
import java.util.List;

public record FriendListResponse (
        List<FriendResponse> friends
){
    public static FriendListResponse of(List<Friend> friends) {
        List<FriendResponse> mapped = friends.stream()
                .map(FriendResponse::of)
                .toList();
        return new FriendListResponse(mapped);

    }

}
