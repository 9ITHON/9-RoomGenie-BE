package team9.demo.model.ai.mask;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Box {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
}