package team9.demo.jpaentity.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import team9.demo.implementation.contact.Contact;
import team9.demo.implementation.contact.PhoneNumber;
import team9.demo.jpaentity.common.BaseEntity;
import team9.demo.model.user.AccessStatus;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@DynamicInsert
@Table(
        name = "user",
        schema = "roomgenie",
        indexes = {
                @Index(name = "user_idx_userid", columnList = "userId")
        }

)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity extends BaseEntity {

    @Id
    private String userId = UUID.randomUUID().toString();

    private String email;

    private String phoneNumber;

    private String password;

    private String name;

    private Long point;

    private Long badge;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private AccessStatus status;

    @Builder
    public UserJpaEntity(
            String phoneNumber,
            String password,
            String name,
            Long point,
            Long badge,
            String email,
            LocalDate birth,
            AccessStatus status
    ) {
        this.userId = UUID.randomUUID().toString();
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.point = point;
        this.badge = badge;
        this.email = email;
        this.birth = birth;
        this.status = status;
    }


    public static UserJpaEntity generate(PhoneNumber phoneNumber, String userName, AccessStatus access) {
        return UserJpaEntity.builder()
                .phoneNumber(phoneNumber.getE164PhoneNumber()) // PhoneNumber → String 변환
                .password("")           // 초기 비밀번호 비워둠
                .name(userName)
                .point(0L)              // 기본값 설정
                .badge(0L)
                .email("")              // 이메일 비워둠
                .birth(null)
                .status(access)
                .build();
    }

    public UserInfo toUser() {
        return UserInfo.of(
                UserId.of(this.userId),
                this.name,
                this.phoneNumber,
                this.email,
                this.password,
                this.birth,
                this.status
        );
    }

    public void increasePoint(long value) {
        if (this.point == null) {
            this.point = 0L;
        }
        this.point += value;
    }


    public void updatePassword(String password) {
        this.password = password;
        this.status = AccessStatus.ACCESS;
    }

    public void updateAccessStatus(AccessStatus accessStatus) {
        this.status = accessStatus;
    }

    public void updateUserName(String name) {
        this.name = name;
    }

}