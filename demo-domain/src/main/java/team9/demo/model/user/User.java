package team9.demo.model.user;

import team9.demo.model.contact.LocalPhoneNumber;

public class User {

    private final UserInfo info;
    private final LocalPhoneNumber localPhoneNumber;

    private User(UserInfo info, LocalPhoneNumber localPhoneNumber) {
        this.info = info;
        this.localPhoneNumber = localPhoneNumber;
    }

    public static User of(UserInfo info, LocalPhoneNumber localPhoneNumber) {
        return new User(info, localPhoneNumber);
    }

    public UserInfo getInfo() {
        return info;
    }

    public LocalPhoneNumber getLocalPhoneNumber() {
        return localPhoneNumber;
    }
}
