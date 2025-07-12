package team9.demo.repository.jpa.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.auth.LoggedInJpaEntity;
import team9.demo.model.token.RefreshToken;
import team9.demo.model.user.UserId;
import team9.demo.repository.auth.LoggedInRepository;
import team9.demo.jparepository.auth.LoggedInJpaRepository;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class LoggedInRepositoryImpl implements LoggedInRepository {

    private final LoggedInJpaRepository loggedInJpaRepository;

    @Override
    @Transactional
    public void remove(String refreshToken) {
        loggedInJpaRepository.deleteByRefreshToken(refreshToken);
    }

    @Override
    public void append(RefreshToken refreshToken, UserId userId) {
        LoggedInJpaEntity entity = LoggedInJpaEntity.generate(refreshToken, userId);
        loggedInJpaRepository.save(entity);
    }

    @Override
    public void update(RefreshToken newRefreshToken, RefreshToken oldRefreshToken) {
        Optional<LoggedInJpaEntity> optional = loggedInJpaRepository.findByRefreshToken(oldRefreshToken.getToken());
        optional.ifPresent(entity -> {
            entity.updateRefreshToken(newRefreshToken);
            loggedInJpaRepository.save(entity);
        });
    }

    @Override
    public RefreshToken read(String refreshToken, UserId userId) {
        return loggedInJpaRepository
                .findByRefreshTokenAndUserId(refreshToken, userId.getId())
                .map(LoggedInJpaEntity::toRefreshToken)
                .orElse(null);
    }
}