package team9.demo.repository.friend;

import team9.demo.model.friend.FriendShip;
import team9.demo.model.user.UserId;

import java.util.List;

public interface FriendShipRepository {

    void createFriend(UserId userId, UserId targetId, String friendName);
    UserId remove(UserId userId, UserId targetId);
    List<FriendShip> reads(UserId userId);
}
