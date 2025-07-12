package team9.demo.repository.jpa.push;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import team9.demo.jpaentity.push.PushNotificationJpaEntity;
import team9.demo.jparepository.push.PushNotificationJpaRepository;
import team9.demo.model.notification.PushInfo;
import team9.demo.model.user.UserInfo;
import team9.demo.repository.push.PushNotificationRepository;

@Repository
public class PushNotificationRepositoryImpl implements PushNotificationRepository {

    private final PushNotificationJpaRepository pushNotificationJpaRepository;

    public PushNotificationRepositoryImpl(PushNotificationJpaRepository pushNotificationJpaRepository) {
        this.pushNotificationJpaRepository = pushNotificationJpaRepository;
    }

    @Override
    @Transactional
    public void remove(PushInfo.Device device) {
        pushNotificationJpaRepository.deleteAllByDeviceIdAndProvider(
                device.getDeviceId(),
                device.getProvider()
        );
    }

    @Override
    public void append(PushInfo.Device device, String appToken, UserInfo userInfo) {
        PushNotificationJpaEntity entity = pushNotificationJpaRepository.findByDeviceIdAndUserId(
                device.getDeviceId(), userInfo.getUserId().getId()
        );

        if (entity == null) {
            PushNotificationJpaEntity newEntity = PushNotificationJpaEntity.generate(appToken, device, userInfo);
            pushNotificationJpaRepository.save(newEntity);
        } else {
            entity.updateAppToken(appToken);
            pushNotificationJpaRepository.save(entity);
        }
    }

    // 필요시 다른 메서드들도 추가
}
