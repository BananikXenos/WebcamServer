package xyz.synse.datacenter.utils.graphics;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GraphicsUtils {
    private final Graphics2D graphics2D;

    public GraphicsUtils(Graphics2D graphics) {
        this.graphics2D = graphics;
    }

    public GraphicsUtils(BufferedImage img) {
        this.graphics2D = img.createGraphics();
    }

    public void circle(int x, int y, int radius) {
        Shape theCircle = new Ellipse2D.Double(x - radius, y - radius, 2.0 * radius, 2.0 * radius);
        graphics2D.fill(theCircle);
    }

    public void line(int x1, int y1, int x2, int y2) {
        graphics2D.drawLine(x1, y1, x2, y2);
    }

    public void color(int rgb) {
        graphics2D.setColor(new Color(rgb));
    }

    public void color(Color color) {
        graphics2D.setColor(color);
    }

    public void text(String str, float x, float y) {
        graphics2D.drawString(str, x, y);
    }

    public void enableAntiAliasing() {
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public void disableAntiAliasing() {
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    public void font(Font font) {
        graphics2D.setFont(font);
    }

    public void font(File file, int size) {
        try {
            InputStream inputStream = new BufferedInputStream(
                    new FileInputStream(file));

            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);

            graphics2D.setFont(font.deriveFont(Font.BOLD, size));

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public void font(InputStream inputStream, int size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);

            graphics2D.setFont(font.deriveFont(Font.BOLD, size));

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Font loadFont(InputStream inputStream, int size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);

            return font.deriveFont(Font.BOLD, size);

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void end() {
        graphics2D.dispose();
    }

    public Font font() {
        return this.graphics2D.getFont();
    }

    public FontMetrics fontMetrics() {
        return this.graphics2D.getFontMetrics();
    }

    public void box(int x, int y, int w, int h) {
        graphics2D.fill(new Rectangle(x, y, w, h));
    }

    public void roundBox(int x, int y, int w, int h, int r) {
        graphics2D.fillRoundRect(x, y, w, h, r, r);
    }

    public void roundBoxOutlined(int x, int y, int w, int h, int r, int outerC, int innerC, int borderSize) {
        color(outerC);
        graphics2D.fillRoundRect(x, y, w, h, r, r);
        color(innerC);
        graphics2D.fillRoundRect(x + borderSize, y + borderSize, w - (borderSize * 2), h - (borderSize * 2), r, r);
    }

    public void complexLines(ArrayList<Point> points) {
        for (Point main : points) {
            for (Point point : points) {
                if (point == main)
                    continue;

                line(main.x, main.y, point.x, point.y);
            }
        }
    }

    public void lines(ArrayList<Point> points) {
        Point lastPoint = null;
        for (Point point : points) {
            if (lastPoint == null) {
                lastPoint = point;
                continue;
            }

            line(lastPoint.x, lastPoint.y, point.x, point.y);
        }
    }

    public void image(BufferedImage img, int x, int y, int width, int height){
        graphics2D.drawImage(img, x, y, width, height, null);
    }

    public void circles(List<Point> points, int radius) {
        for (Point point : points) {
            circle(point.x, point.y, radius);
        }
    }

    public void stroke(BasicStroke basicStroke) {
        graphics2D.setStroke(basicStroke);
    }
}
