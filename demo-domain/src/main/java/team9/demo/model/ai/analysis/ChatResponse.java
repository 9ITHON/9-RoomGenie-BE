package team9.demo.model.ai.analysis;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private List<Choice> choices;

    public String getResultMessage() {
        if (choices == null || choices.isEmpty()) {
            return "";
        }
        TextMessage message = choices.get(0).getMessage();
        if (message == null) {
            return "";
        }
        return message.getContent();
    }
}