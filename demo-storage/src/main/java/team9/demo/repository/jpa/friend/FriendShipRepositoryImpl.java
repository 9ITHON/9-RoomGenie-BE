package team9.demo.repository.jpa.friend;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import team9.demo.jpaentity.friend.FriendShipId;
import team9.demo.jpaentity.friend.FriendShipJpaEntity;
import team9.demo.jparepository.friend.FriendShipJpaRepository;
import team9.demo.model.friend.FriendShip;
import team9.demo.model.friend.FriendShipStatus;
import team9.demo.model.user.UserId;
import team9.demo.repository.friend.FriendShipRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendShipRepositoryImpl implements FriendShipRepository {

    private final FriendShipJpaRepository friendShipJpaRepository;


    @Override
    @Transactional
    public void createFriend(UserId userId, UserId targetId, String FriendName) {
        FriendShipId id = FriendShipId.of(userId, targetId);

        friendShipJpaRepository.findById(id)
                .map(entity -> {                 // 이미 있으면 → 상태만 변경
                    entity.allowedFriend();      // FRIEND
                    return null;
                })
                .orElseGet(() -> {
                    // 없으면 → 새로 생성하여 FRIEND로
                    FriendShipJpaEntity created = FriendShipJpaEntity.generate(
                            userId,
                            targetId,
                            FriendName,
                            FriendShipStatus.FRIEND
                    );
                    friendShipJpaRepository.save(created);
                    return null;
                });
    }

    @Override
    @Transactional
    public UserId remove(UserId userId, UserId friendId) {
        return friendShipJpaRepository.findById(FriendShipId.of(userId, friendId))
                .map(entity -> {
                    entity.updateDelete();
                    return userId;
                })
                .orElse(null);
    }


    @Override
    public List<FriendShip> reads(UserId userId) {
        return friendShipJpaRepository.findAllByIdUserId(userId.getId())
                .stream()
                .map(FriendShipJpaEntity::toFriendShip)
                .toList();
    }

}
