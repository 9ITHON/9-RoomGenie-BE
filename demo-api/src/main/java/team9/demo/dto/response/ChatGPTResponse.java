package team9.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import team9.demo.model.ai.analysis.TextMessage;
import team9.demo.model.ai.analysis.Choice;

import java.util.List;

public record ChatGPTResponse(
        @JsonProperty("choices") List<Choice> choices
) {
    public static ChatGPTResponse of(String resultMessage) {
        return new ChatGPTResponse(List.of(new Choice(0, new TextMessage("assistant", resultMessage))));
    }

    public static ChatGPTResponse of(String imageUrl, String analysisText) {
        String combinedContent = imageUrl + "\n\nGPT Analysis:\n" + analysisText;
        return new ChatGPTResponse(List.of(new Choice(0, new TextMessage("assistant", combinedContent))));
    }
}
