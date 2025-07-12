package team9.demo.jparepository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.user.UserJpaEntity;
import team9.demo.model.user.AccessStatus;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {

    Optional<UserJpaEntity> findUserJpaEntityByPhoneNumberAndStatus(String phoneNumber, AccessStatus status);

    Optional<UserJpaEntity> findByUserIdAndStatus(String userId, AccessStatus status);

    List<UserJpaEntity> findUserJpaEntitiesByPhoneNumberInAndStatus(List<String> phoneNumbers, AccessStatus status);

    List<UserJpaEntity> findAllByUserIdInAndStatus(List<String> userIds, AccessStatus status);
}