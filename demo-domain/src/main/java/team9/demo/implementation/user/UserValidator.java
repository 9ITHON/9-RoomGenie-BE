package team9.demo.implementation.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.error.ConflictException;
import team9.demo.error.ErrorCode;
import team9.demo.implementation.contact.Contact;
import team9.demo.model.user.AccessStatus;
import team9.demo.model.user.UserId;
import team9.demo.repository.user.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void isNotAlreadyCreated(Contact contact) {
        if (userRepository.readByContact(contact, AccessStatus.ACCESS) != null) {
            throw new ConflictException(ErrorCode.USER_ALREADY_CREATED);
        }
    }

    public void isAlreadyCreated(Contact contact) {
        if (userRepository.readByContact(contact, AccessStatus.ACCESS) == null) {
            throw new ConflictException(ErrorCode.USER_NOT_CREATED);
        }
    }


    public void nameExists(String name) {
        if (userRepository.searchUser(name).isPresent()) {
            throw new ConflictException(ErrorCode.USER_NAME_OVERLAP);
        }
    }

    public void userExists(UserId userId) {
        if (!userRepository.userExists(userId)){
            throw new ConflictException(ErrorCode.USER_NOT_FOUND);
        }
    }

}