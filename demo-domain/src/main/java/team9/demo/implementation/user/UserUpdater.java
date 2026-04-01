package team9.demo.implementation.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.model.user.UserId;
import team9.demo.repository.user.UserRepository;

import java.time.LocalDate;


@Component
@RequiredArgsConstructor
public class UserUpdater {
    private final UserRepository userRepository;


    public void updateBirthdayAndEmail(UserId userId, String email, LocalDate birthday) {
        userRepository.updateBirthdayAndEmail(userId, email, birthday);
    }


}