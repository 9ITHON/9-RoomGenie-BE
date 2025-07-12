package team9.demo.jparepository.push;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.demo.jpaentity.push.PushNotificationJpaEntity;
import team9.demo.model.notification.NotificationStatus;
import team9.demo.model.notification.PushInfo;

import java.util.List;

public interface PushNotificationJpaRepository extends JpaRepository<PushNotificationJpaEntity, String> {

    void deleteAllByDeviceIdAndProvider(String deviceId, PushInfo.Provider deviceProvider);

    List<PushNotificationJpaEntity> findAllByUserId(String userId);

    List<PushNotificationJpaEntity> findAllByUserIdAndChatStatus(String userId, NotificationStatus chatStatus);

    List<PushNotificationJpaEntity> findAllByUserIdAndScheduleStatus(String userId, NotificationStatus scheduleStatus);

    List<PushNotificationJpaEntity> findAllByUserIdIn(List<String> userIds);

    List<PushNotificationJpaEntity> findAllByUserIdInAndChatStatus(List<String> userIds, NotificationStatus chatStatus);

    List<PushNotificationJpaEntity> findAllByUserIdInAndScheduleStatus(List<String> userIds, NotificationStatus scheduleStatus);

    PushNotificationJpaEntity findByDeviceIdAndUserId(String deviceId, String userId);
}