package team9.demo.facade.friend;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team9.demo.implementation.friend.friend.FriendAggregator;
import team9.demo.implementation.user.UserReader;
import team9.demo.implementation.user.UserValidator;
import team9.demo.model.friend.Friend;
import team9.demo.model.friend.FriendShip;
import team9.demo.model.user.AccessStatus;
import team9.demo.model.user.User;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;
import team9.demo.service.friend.FriendShipService;
import team9.demo.service.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendFacade {

    private final FriendShipService friendShipService;
    private final UserValidator userValidator;
    private final UserService userService;
    private final FriendAggregator friendAggregator;
    private final UserReader userReader;

    public void createFriend(UserId userId, UserId targetId){
        userValidator.userExists(userId);
        UserInfo userInfo = userReader.read(targetId, AccessStatus.ACCESS);
        friendShipService.createFriend(userId, targetId, userInfo.getUserName());
    }

    public void deleteFriend(UserId userId, UserId targetId){
        userValidator.userExists(userId);
        friendShipService.deleteFriend(userId, targetId);
    }

    public List<Friend> getFriends(UserId userId){
        List<FriendShip> friendShips = friendShipService.getFriendShips(userId);
        List<UserId> friendIds = friendShips.stream()
                .map(FriendShip::getFriendId)
                .toList();
        List<User> users = userService.getUsers(friendIds, AccessStatus.ACCESS);
        return friendAggregator.aggregates(users, friendShips);
    }


}
