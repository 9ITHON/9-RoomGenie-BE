package team9.demo.model.user;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public final class UserInfo {

    private final UserId userId;
    private final String userName;
    private final String phoneNumber;
    private final String email;
    private final String password;
    private final LocalDate birth;
    private final AccessStatus status;


    private UserInfo(
            UserId userId,
            String userName,
            String phoneNumber,
            String email,
            String password,
            LocalDate birth,
            AccessStatus status
    ) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.birth = birth;
        this.status = status;
    }

    public static UserInfo of(
            UserId userId,
            String userName,
            String phoneNumber,
            String email,
            String password,
            LocalDate birth,
            AccessStatus status
    ) {
        return new UserInfo(
                userId,
                userName,
                phoneNumber,
                email,
                password,
                birth,
                status
        );
    }
}
