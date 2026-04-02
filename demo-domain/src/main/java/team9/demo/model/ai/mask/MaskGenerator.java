package team9.demo.model.ai.mask;



import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;


public class MaskGenerator {

    private static final int EXPAND = 20;
    private static final int ROUND_ARC = 30;

    public static byte[] createMask(List<Box> boxes, int width, int height) {
        BufferedImage mask = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = mask.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        for (Box box : boxes) {
            int x = Math.max(0, box.getX() - EXPAND);
            int y = Math.max(0, box.getY() - EXPAND);
            int w = Math.min(width, box.getX() + box.getWidth() + EXPAND) - x;
            int h = Math.min(height, box.getY() + box.getHeight() + EXPAND) - y;

            g2d.fillRoundRect(x, y, w, h, ROUND_ARC, ROUND_ARC);
        }

        g2d.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(mask, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Mask generation failed", e);
        }
    }
}