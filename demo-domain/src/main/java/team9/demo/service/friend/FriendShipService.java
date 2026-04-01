package team9.demo.service.friend;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team9.demo.implementation.friend.friendship.FriendShipAppender;
import team9.demo.implementation.friend.friendship.FriendShipReader;
import team9.demo.implementation.friend.friendship.FriendShipRemover;
import team9.demo.model.friend.FriendShip;
import team9.demo.model.user.UserId;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendShipService {

    private final FriendShipAppender friendShipAppender;
    private final FriendShipRemover friendShipRemover;
    private final FriendShipReader friendShipReader;

    public void createFriend(UserId userId, UserId targetId, String friendName){
        friendShipAppender.createFriend(userId, targetId, friendName);
    }

    public void deleteFriend(UserId userId, UserId targetId){
        friendShipRemover.remove(userId, targetId);
    }

    public List<FriendShip> getFriendShips(UserId userId){
        return friendShipReader.reads(userId);
    }


}
