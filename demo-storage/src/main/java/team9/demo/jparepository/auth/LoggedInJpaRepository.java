package team9.demo.jparepository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.demo.jpaentity.auth.LoggedInJpaEntity;

import java.util.Optional;

public interface LoggedInJpaRepository extends JpaRepository<LoggedInJpaEntity, String> {

    void deleteByRefreshToken(String refreshToken);

    Optional<LoggedInJpaEntity> findByRefreshToken(String refreshToken);

    Optional<LoggedInJpaEntity> findByRefreshTokenAndUserId(String refreshToken, String userId);
}
