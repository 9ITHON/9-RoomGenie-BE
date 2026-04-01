package team9.demo.repository.jpa.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.error.ConflictException;
import team9.demo.error.ErrorCode;
import team9.demo.implementation.contact.Contact;
import team9.demo.implementation.contact.PhoneNumber;
import team9.demo.jpaentity.user.UserJpaEntity;
import team9.demo.jparepository.user.UserJpaRepository;
import team9.demo.model.user.AccessStatus;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;
import team9.demo.repository.user.UserRepository;

import java.time.LocalDate;
import java.util.List;
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
        throw new ConflictException(ErrorCode.NOT_SUPPORT_CONTACT_TYPE);
    }

    @Override
    public UserInfo readByEmail(String email, AccessStatus status) {
            return userJpaRepository.findUserJpaEntityByEmailAndStatus(
                            email, status
                    ).map(UserJpaEntity::toUser) // ✅ 인스턴스 기준 메서드 참조
                    .orElse(null);
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

        throw new ConflictException(ErrorCode.NOT_SUPPORT_CONTACT_TYPE);
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

    @Override
    public void updateBirthdayAndEmail(UserId userId, String email, LocalDate birthday) {
        userJpaRepository.findById(userId.getId())
                .ifPresent(entity -> {
                    entity.updateBirthday(birthday);
                    entity.updateEmail(email);
                    userJpaRepository.save(entity);

                });
    }


    @Override
    public Optional<UserId> searchUser(String name) {
        return userJpaRepository.findByName(name).map(e -> UserId.of(e.getUserId()));
    }

    @Override
    public boolean userExists(UserId userId) {
        return userJpaRepository.findById(userId.getId()).isPresent();
    }

    @Override
    public List<UserInfo> reads(List<UserId> userIds, AccessStatus accessStatus){
        List<String> ids = userIds.stream()
                .map(UserId::getId)
                .toList();

        List<UserJpaEntity> entities = userJpaRepository.findAllByUserIdInAndStatus(ids, accessStatus);

        return entities.stream()
                .map(UserJpaEntity::toUser)
                .toList();



    }
    @Override
    public UserInfo read(UserId userId, AccessStatus status){
        return userJpaRepository.findByUserIdAndStatus(userId.getId(), status)
                .map(UserJpaEntity::toUser)
                .orElse(null);
    }

}