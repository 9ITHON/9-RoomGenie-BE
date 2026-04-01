package team9.demo.jpaentity.friend;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.mapping.PersistentEntity;
import team9.demo.jpaentity.common.BaseEntity;
import team9.demo.model.friend.FriendShip;
import team9.demo.model.friend.FriendShipStatus;
import team9.demo.model.user.UserId;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Entity
@Table(
        name = "friend_ship",
        schema = "roomgenie",
        indexes = {
                @Index(name = "friend_ship_idx_user_id", columnList = "user_id")
        }
)
public class FriendShipJpaEntity extends BaseEntity {

    @EmbeddedId
    private FriendShipId id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FriendShipStatus status;

    private FriendShipJpaEntity(FriendShipId id, String name, FriendShipStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public static FriendShipJpaEntity generate(UserId userId, UserId targetUserId, String name, FriendShipStatus status) {
        return new FriendShipJpaEntity(
                FriendShipId.of(userId, targetUserId),
                name,
                status
        );
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDelete(){
        this.status = FriendShipStatus.NORMAL;
    }


    public void allowedFriend() {
        this.status = FriendShipStatus.FRIEND;
    }

    public FriendShip toFriendShip() {
        return FriendShip.of(
                UserId.of(id.getUserId()),
                UserId.of(id.getFriendId()),
                name,
                status
        );
    }

}
