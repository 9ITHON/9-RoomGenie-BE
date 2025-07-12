package team9.demo.model.ai.mask;



import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;


public class MaskGenerator {

    private static final int PADDING = 15; // 박스 주변 확장값 (조정 가능)

    public static byte[] createMask(List<Box> boxes, int width, int height) {
        BufferedImage mask = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = mask.createGraphics();

        // 앤티앨리어싱 등 부가 옵션 추가 (optional)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 흰색으로 마스크 박스 그리기
        g2d.setColor(Color.WHITE);
        for (Box box : boxes) {
            int x1 = Math.max(0, box.getX() - PADDING);
            int y1 = Math.max(0, box.getY() - PADDING);
            int x2 = Math.min(width, box.getX() + box.getWidth() + PADDING);
            int y2 = Math.min(height, box.getY() + box.getHeight() + PADDING);
            int w = x2 - x1;
            int h = y2 - y1;

            int expand = 20;  // 넓게 잡아버리기
            g2d.fillRoundRect(
                    box.getX() - expand,
                    box.getY() - expand,
                    box.getWidth() + 2 * expand,
                    box.getHeight() + 2 * expand,
                    30, 30  // 라운딩 크게 줘서 자연스럽게
            );
        }

        g2d.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(mask, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Mask generation failed", e);
        }
    }
}