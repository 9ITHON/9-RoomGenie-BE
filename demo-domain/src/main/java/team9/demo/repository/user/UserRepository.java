package team9.demo.repository.user;

import team9.demo.implementation.contact.Contact;
import team9.demo.model.user.AccessStatus;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface UserRepository {
    UserInfo readByEmail(String email, AccessStatus status);
    UserInfo readByContact(Contact contact, AccessStatus status);
    UserInfo append(Contact contact, String userName);
    UserInfo remove(UserId userId);
    void appendPassword(UserId userId, String password);
    void updateBirthdayAndEmail(UserId userId, String email, LocalDate birthday);
    Optional<UserId> searchUser(String name);
    boolean userExists(UserId userId);
    List<UserInfo> reads(List<UserId> userIds, AccessStatus accessStatus);
    UserInfo read(UserId userId, AccessStatus accessStatus);
}