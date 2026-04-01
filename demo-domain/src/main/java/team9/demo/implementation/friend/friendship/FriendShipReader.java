package team9.demo.implementation.friend.friendship;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.model.friend.FriendShip;
import team9.demo.model.user.UserId;
import team9.demo.repository.friend.FriendShipRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendShipReader {
    private final FriendShipRepository friendShipRepository;


    public List<FriendShip> reads(UserId userId){
        return friendShipRepository.reads(userId);
    }


}
