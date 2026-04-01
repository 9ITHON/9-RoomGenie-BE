package team9.demo.dto.response.friend;


import team9.demo.model.friend.Friend;

import java.util.Locale;

public record FriendResponse (
        String friendId,
        String name,
        String email,
        String status,
        String birthday
){
    public static FriendResponse of(Friend friend){
        var info = friend.getUser().getInfo();
        var email  = friend.getUser().getEmail();

        String birthdayStr = info.getBirth() != null ? info.getBirth().toString() : "";
        String statusStr = friend.getStatus().name().toLowerCase(Locale.ROOT);


        return new FriendResponse(
                info.getUserId().getId(),
                friend.getName(),
                email,
                statusStr,
                birthdayStr

        );


    }



}
