package team9.demo.model.friend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.demo.model.user.User;

@Getter
@AllArgsConstructor
public final class Friend {
    private final User user;
    private final String name;
    private final FriendShipStatus status;


    public static Friend of(User friend, FriendShip friendShip){
        return new Friend(
                friend,
                friendShip.getFriendName(),
                friendShip.getStatus()
        );
    }


}
