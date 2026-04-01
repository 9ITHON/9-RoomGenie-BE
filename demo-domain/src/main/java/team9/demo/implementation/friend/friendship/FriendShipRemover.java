package team9.demo.implementation.friend.friendship;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team9.demo.error.ErrorCode;
import team9.demo.error.NotFoundException;
import team9.demo.model.user.UserId;
import team9.demo.repository.friend.FriendShipRepository;

@Component
@RequiredArgsConstructor
public class FriendShipRemover {

    private final FriendShipRepository friendShipRepository;

    @Transactional
    public void remove(UserId userId, UserId targetId){
        if(friendShipRepository.remove(userId, targetId) == null){
            throw new NotFoundException(ErrorCode.FRIEND_NOT_FOUND);
        }
    }


}
