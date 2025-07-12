package team9.demo.repository.jpa.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.implementation.contact.Contact;
import team9.demo.implementation.contact.PhoneNumber;
import team9.demo.jpaentity.user.UserJpaEntity;
import team9.demo.jparepository.user.UserJpaRepository;
import team9.demo.model.user.AccessStatus;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;
import team9.demo.repository.user.UserRepository;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserInfo readByContact(Contact contact, AccessStatus status) {
        if (contact instanceof PhoneNumber phoneNumber) {
            return userJpaRepository.findUserJpaEntityByPhoneNumberAndStatus(
                            phoneNumber.getE164PhoneNumber(), status
                    ).map(UserJpaEntity::toUser) // ✅ 인스턴스 기준 메서드 참조
                    .orElse(null);
        }
        throw new IllegalArgumentException("지원되지 않는 Contact 타입입니다.");
    }

    @Override
    public UserInfo append(Contact contact, String userName) {
        if (contact instanceof PhoneNumber phoneNumber) {
            Optional<UserJpaEntity> optional = userJpaRepository.findUserJpaEntityByPhoneNumberAndStatus(
                    phoneNumber.getE164PhoneNumber(),
                    AccessStatus.NEED_CREATE_PASSWORD
            );

            if (optional.isPresent()) {
                UserJpaEntity entity = optional.get();
                entity.updateUserName(userName);
                userJpaRepository.save(entity);
                return entity.toUser();
            } else {
                // ✅ phoneNumber 로 캐스팅된 후 넘겨야 generate에 맞음
                UserJpaEntity newEntity = UserJpaEntity.generate(phoneNumber, userName, AccessStatus.NEED_CREATE_PASSWORD);
                return userJpaRepository.save(newEntity).toUser();
            }
        }

        throw new IllegalArgumentException("Unsupported contact type: " + contact.getClass().getSimpleName());
    }


    @Override
    public void appendPassword(UserId userId, String password) {
        Optional<UserJpaEntity> optional = userJpaRepository.findById(userId.getId());

        optional.ifPresent(entity -> {
            entity.updateAccessStatus(AccessStatus.ACCESS);
            entity.updatePassword(password);
            userJpaRepository.save(entity);
        });
    }

    @Override
    public UserInfo remove(UserId userId) {
        return userJpaRepository.findById(userId.getId())
                .map(entity -> {
                    entity.updateAccessStatus(AccessStatus.DELETE);
                    userJpaRepository.save(entity);
                    return entity.toUser();
                })
                .orElse(null);
    }


}