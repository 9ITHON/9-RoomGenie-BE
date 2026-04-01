package team9.demo.model.friend;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import team9.demo.model.user.UserId;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FriendShip {
    private UserId userId;
    private UserId friendId;
    private String friendName;
    private FriendShipStatus status;

    public static FriendShip of(UserId userId, UserId friendId, String friendName, FriendShipStatus status) {
        return new FriendShip(
                userId, friendId, friendName, status
        );
    }
}
