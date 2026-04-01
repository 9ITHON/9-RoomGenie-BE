package team9.demo.repository.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.jpaentity.analysis.AnalysisResultJpaEntity;
import team9.demo.jparepository.result.AnalysisResultRepository;

import team9.demo.model.user.UserId;
import team9.demo.service.user.AiAnalysisGenerator;


@Component
@RequiredArgsConstructor
public class AiAnalysisGeneratorImpl implements AiAnalysisGenerator {

    private final AnalysisResultRepository analysisResultRepository;

    @Override
    public void saveAnalysisResult(UserId userId, String resultText, String imageUrl) {
        AnalysisResultJpaEntity entity = AnalysisResultJpaEntity.generate(userId, resultText, imageUrl);
        analysisResultRepository.save(entity);
    }
}