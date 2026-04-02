package team9.demo.repository.jpa.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.error.ErrorCode;
import team9.demo.error.NotFoundException;
import team9.demo.jpaentity.user.UserJpaEntity;
import team9.demo.jparepository.user.UserJpaRepository;
import team9.demo.repository.user.UserPointRepository;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public long increasePoint(String userId, long point) {
        UserJpaEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        user.increasePoint(point);
        userJpaRepository.save(user);
        return user.getPoint();
    }
}
