package team9.demo.model.ai.generate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageGenerationRequest {
    private String prompt;
    private int n;
    private String size;
}
