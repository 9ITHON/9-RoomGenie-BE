package team9.demo.model.ai.generate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class ImageGenerationResponse {
    private long created;
    private List<ImageData> data;

    @Getter
    @Setter
    public static class ImageData {
        private String url;
    }
}

