package team9.demo.dto.request.ai;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import team9.demo.model.ai.analysis.RoleMessage;

@Getter
@Setter
@AllArgsConstructor
public class ImageMessage extends RoleMessage {
    private List<Content> content;

    public ImageMessage(String role, List<Content> content) {
        super(role);
        this.content = content;
    }
}