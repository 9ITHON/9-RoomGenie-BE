package team9.demo.implementation.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import team9.demo.error.ErrorCode;
import team9.demo.error.NotFoundException;
import team9.demo.implementation.contact.Contact;
import team9.demo.model.mission.TodayMissionInfo;
import team9.demo.model.user.AccessStatus;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;
import team9.demo.repository.mission.TodayMissionQueryRepository;
import team9.demo.repository.push.PushNotificationRepository;
import team9.demo.repository.user.UserRepository;

import java.util.List;


@Component
@RequiredArgsConstructor
public class UserReader {

    private final UserRepository userRepository;


    public UserInfo readByEmail(String email, AccessStatus accessStatus) {
        UserInfo userInfo = userRepository.readByEmail(email, accessStatus);
        if (userInfo == null) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        return userInfo;
    }

    public UserId searchUser(String name) {
        return userRepository.searchUser(name)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }
    public UserInfo read(UserId userId, AccessStatus accessStatus) {
        UserInfo userInfo = userRepository.read(userId, accessStatus);
        if (userInfo == null) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        return userInfo;
    }


    public List<UserInfo> reads(List<UserId> userIds, AccessStatus accessStatus){
        return userRepository.reads(userIds, accessStatus);
    }


}