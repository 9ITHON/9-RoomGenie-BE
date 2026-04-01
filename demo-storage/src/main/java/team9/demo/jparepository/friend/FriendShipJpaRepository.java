package team9.demo.jparepository.friend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.friend.FriendShipId;
import team9.demo.jpaentity.friend.FriendShipJpaEntity;
import team9.demo.model.friend.Friend;
import team9.demo.model.friend.FriendShip;
import team9.demo.model.user.UserId;

import java.util.List;

@Repository
public interface FriendShipJpaRepository extends JpaRepository<FriendShipJpaEntity, FriendShipId> {
    List<FriendShipJpaEntity> findAllByIdUserId(String userId);
}
