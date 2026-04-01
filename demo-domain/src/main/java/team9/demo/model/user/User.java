package team9.demo.model.user;

import lombok.Getter;
import team9.demo.model.contact.LocalPhoneNumber;

@Getter
public class User {

    private final UserInfo info;
    private final String email;

    private User(UserInfo info, String email) {
        this.info = info;
        this.email = email;
    }

    public static User of(UserInfo info, String email) {
        return new User(info, email);
    }

}
