package team9.demo.implementation.friend.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.error.ConflictException;
import team9.demo.error.ErrorCode;
import team9.demo.model.user.UserId;
import team9.demo.repository.friend.FriendShipRepository;

@Component
@RequiredArgsConstructor
public class FriendShipAppender {

    private final FriendShipRepository friendShipRepository;

    public void createFriend(UserId userId, UserId targetId, String friendName) {
        if (userId.getId().equals(targetId.getId())) {
            throw new ConflictException(ErrorCode.FRIEND_MYSELF);
        }
        friendShipRepository.createFriend(userId, targetId, friendName);
    }


}
