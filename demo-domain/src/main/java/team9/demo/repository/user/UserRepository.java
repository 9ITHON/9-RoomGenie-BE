package team9.demo.repository.user;

import team9.demo.implementation.contact.Contact;
import team9.demo.model.user.AccessStatus;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;

public interface UserRepository {
    UserInfo readByContact(Contact contact, AccessStatus status);
    UserInfo append(Contact contact, String userName);
    UserInfo remove(UserId userId);
    void appendPassword(UserId userId, String password);
}