package team9.demo.jparepository.result;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.analysis.AnalysisResultJpaEntity;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResultJpaEntity, String> {
    // 조회, 삭제용 커스텀 메서드가 필요하면 여기에 추가 가능
}
