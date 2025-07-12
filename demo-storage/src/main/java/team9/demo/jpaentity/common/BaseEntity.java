package team9.demo.jpaentity.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@MappedSuperclass // 이 클래스가 데이터베이스 테이블과 직접 매핑되지 않고, 다른 엔티티 클래스에 매핑 정보를 제공함을 나타냅니다.
@EntityListeners(AuditingEntityListener.class) // JPA 엔티티의 생명주기 이벤트를 감지하여 Auditing 기능을 활성화합니다.
public abstract class BaseEntity {

    @CreatedDate // 엔티티가 생성될 때 현재 시간을 자동으로 주입합니다.
    @Column(name = "created_at", columnDefinition = "datetime(6)", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티가 업데이트될 때 현재 시간을 자동으로 주입합니다.
    @Column(name = "updated_at", columnDefinition = "datetime(6)")
    private LocalDateTime updatedAt;

    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }




}
