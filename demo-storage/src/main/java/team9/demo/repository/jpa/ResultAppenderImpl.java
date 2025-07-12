package team9.demo.repository.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.jpaentity.analysis.AnalysisResultJpaEntity;
import team9.demo.jparepository.result.AnalysisResultRepository;

import team9.demo.model.user.UserId;
import team9.demo.service.user.ResultAppender;


@Component
@RequiredArgsConstructor
public class ResultAppenderImpl implements ResultAppender {

    private final AnalysisResultRepository analysisResultRepository;

    @Override
    public void saveAnalysisResult(UserId userId, String resultText, String imageUrl) {
        AnalysisResultJpaEntity entity = AnalysisResultJpaEntity.generate(userId, resultText, imageUrl);
        analysisResultRepository.save(entity);
    }
}