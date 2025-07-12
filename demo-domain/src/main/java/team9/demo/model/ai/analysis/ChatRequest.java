package team9.demo.model.ai.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String model;

    // ✅ 'prompt' 대신 'messages'로 수정
    private List<Message> messages;

    @JsonProperty("max_tokens")
    private int maxTokens;
}
