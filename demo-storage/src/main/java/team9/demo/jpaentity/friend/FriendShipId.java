package team9.demo.jpaentity.friend;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.demo.model.user.UserId;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class FriendShipId implements Serializable {

    @Column
    private String userId;

    @Column
    private String friendId;

    public static FriendShipId of(UserId userId, UserId friendId) {
        return new FriendShipId(userId.getId(), friendId.getId());
    }


}
