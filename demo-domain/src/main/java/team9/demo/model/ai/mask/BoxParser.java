package team9.demo.model.ai.mask;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoxParser {

    private static final Pattern BOX_PATTERN = Pattern.compile("\\(x=(\\d+), y=(\\d+), w=(\\d+), h=(\\d+)\\)");

    public static List<Box> parse(String text) {
        List<Box> boxes = new ArrayList<>();
        Matcher matcher = BOX_PATTERN.matcher(text);

        while (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            int w = Integer.parseInt(matcher.group(3));
            int h = Integer.parseInt(matcher.group(4));
            boxes.add(new Box(x, y, w, h));
        }

        return boxes;
    }
}