package team9.demo.model.ai.mask;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link MaskGenerator}의 마스크 후처리 동작 검증.
 * <p>
 * YOLO bounding box를 그대로 사용하면 LAMA 인페인팅 결과에 가장자리 잔상이 남는 문제를 해결하기 위해
 * 1) 박스를 20px 확장하고
 * 2) 라운드 처리 + AntiAliasing을 적용했다.
 * <p>
 * 본 테스트는 README "Troubleshooting > LAMA 인페인팅 가장자리 잔상" 항목의
 * 후처리 로직이 의도대로 동작하는지를 보장한다.
 */
class MaskGeneratorTest {

    private static final int EXPAND = 20;
    private static final int CANVAS_WIDTH = 200;
    private static final int CANVAS_HEIGHT = 200;

    private BufferedImage decode(byte[] pngBytes) throws IOException {
        try (ByteArrayInputStream in = new ByteArrayInputStream(pngBytes)) {
            return ImageIO.read(in);
        }
    }

    /** 그레이스케일 마스크에서 (x,y)가 흰색(=마스킹 영역)인지 확인. */
    private boolean isMasked(BufferedImage mask, int x, int y) {
        // BufferedImage.TYPE_BYTE_GRAY 는 getRGB의 R/G/B가 모두 동일한 밝기 값
        return new Color(mask.getRGB(x, y)).getRed() > 200;
    }

    @Test
    @DisplayName("빈 박스 리스트도 안전하게 처리되어 전부 검은(미마스킹) 그레이 PNG를 반환한다")
    void createMask_emptyBoxes_returnsBlankMask() throws IOException {
        byte[] result = MaskGenerator.createMask(Collections.emptyList(), CANVAS_WIDTH, CANVAS_HEIGHT);

        BufferedImage mask = decode(result);
        assertThat(mask).isNotNull();
        assertThat(mask.getWidth()).isEqualTo(CANVAS_WIDTH);
        assertThat(mask.getHeight()).isEqualTo(CANVAS_HEIGHT);
        assertThat(isMasked(mask, 100, 100)).isFalse();
    }

    @Nested
    @DisplayName("박스 확장(EXPAND=20px)")
    class BoxExpansion {

        @Test
        @DisplayName("박스 외곽 EXPAND 픽셀 안쪽까지 마스킹 영역에 포함된다")
        void expansion_extendsOutsideOriginalBox() throws IOException {
            // 캔버스 중앙에 충분히 안쪽으로 떨어진 박스 — 경계 클램핑 영향을 배제
            Box box = new Box(80, 80, 40, 40); // (80,80) ~ (120,120)
            byte[] result = MaskGenerator.createMask(List.of(box), CANVAS_WIDTH, CANVAS_HEIGHT);

            BufferedImage mask = decode(result);

            // 원본 박스 내부는 당연히 마스킹
            assertThat(isMasked(mask, 100, 100)).isTrue();

            // 원본 박스 바로 바깥(왼쪽으로 EXPAND-5=15px)도 확장 영역에 포함
            assertThat(isMasked(mask, 80 - 15, 100)).isTrue();

            // 확장 한계 바깥(왼쪽으로 EXPAND+10=30px)은 마스킹되지 않아야 함
            // (라운드 코너 영향을 피하기 위해 y는 박스 중앙에서 확인)
            assertThat(isMasked(mask, 80 - (EXPAND + 10), 100)).isFalse();
        }
    }

    @Nested
    @DisplayName("경계 클램핑")
    class BoundaryClamping {

        @Test
        @DisplayName("좌상단(0,0)에 붙은 박스를 확장해도 음수 좌표로 나가지 않는다")
        void clamping_topLeftCorner_doesNotOverflowNegative() throws IOException {
            Box box = new Box(0, 0, 30, 30);

            // 예외 없이 정상적으로 PNG가 생성되어야 함 (음수 좌표로 fillRoundRect 호출 시 IllegalArgumentException 위험 차단)
            byte[] result = MaskGenerator.createMask(List.of(box), CANVAS_WIDTH, CANVAS_HEIGHT);

            BufferedImage mask = decode(result);
            assertThat(mask).isNotNull();
            // 좌상단 모서리는 라운드 처리로 살짝 깎일 수 있어 박스 내부 중앙 픽셀로 검증
            assertThat(isMasked(mask, 15, 15)).isTrue();
        }

        @Test
        @DisplayName("우하단 경계에 붙은 박스를 확장해도 캔버스 크기를 넘지 않는다")
        void clamping_bottomRightCorner_doesNotOverflowCanvas() throws IOException {
            Box box = new Box(170, 170, 30, 30); // 우하단까지 꽉 차는 박스

            // 예외 없이 정상 생성 — 확장 후에도 width/height를 초과하지 않아야 함
            byte[] result = MaskGenerator.createMask(List.of(box), CANVAS_WIDTH, CANVAS_HEIGHT);

            BufferedImage mask = decode(result);
            assertThat(mask.getWidth()).isEqualTo(CANVAS_WIDTH);
            assertThat(mask.getHeight()).isEqualTo(CANVAS_HEIGHT);
            assertThat(isMasked(mask, 185, 185)).isTrue();
        }
    }

    @Test
    @DisplayName("여러 박스가 모두 마스킹 영역에 반영된다")
    void createMask_multipleBoxes_allRendered() throws IOException {
        Box box1 = new Box(40, 40, 30, 30);
        Box box2 = new Box(140, 140, 30, 30);

        byte[] result = MaskGenerator.createMask(List.of(box1, box2), CANVAS_WIDTH, CANVAS_HEIGHT);

        BufferedImage mask = decode(result);
        assertThat(isMasked(mask, 55, 55)).isTrue();   // box1 내부
        assertThat(isMasked(mask, 155, 155)).isTrue(); // box2 내부
        assertThat(isMasked(mask, 100, 100)).isFalse(); // 두 박스 사이 간격
    }
}
