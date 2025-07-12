package team9.demo.model.ai.analysis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TextMessage extends RoleMessage {
    private String content;

    public TextMessage(String role, String content) {
        super(role);
        this.content = content;
    }
}