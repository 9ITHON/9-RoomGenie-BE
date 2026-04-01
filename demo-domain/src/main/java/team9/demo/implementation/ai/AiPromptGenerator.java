package team9.demo.implementation.ai;

import org.springframework.stereotype.Component;

@Component
public class AiPromptGenerator {

    public String missionVerification(String missionContent) {
        return "오늘의 미션은 다음과 같습니다: [" + missionContent + "]. " +
                "아래의 두 이미지를 비교해 이 미션이 성공적으로 수행되었는지 평가해주세요. " +
                "방이 깨끗한 것과 미션 성공 여부는 다르니 미션에 집중해주세요. " +
                "반드시 응답의 첫 줄에 [RESULT:SUCCESS] 또는 [RESULT:FAIL]을 포함해주세요. " +
                "그 다음 줄부터 평가 내용을 작성해주세요.";
    }

    public String roomCleaningGuide() {
        return "해당 이미지를 보고 분석 후 어지럽혀진 방을 깔끔하게 정리할 수 있도록 가이드를 작성해 주세요.";
    }

    public String lamaInpainting() {
        return "Remove all clutter from the surface. Keep walls and furniture unchanged.";
    }
}
