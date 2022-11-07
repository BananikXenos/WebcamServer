package xyz.synse.datacenter.utils.image;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class ImageUtils {
    public static BufferedImage rotateImage(BufferedImage original, double angle) {
        double theta = Math.toRadians(angle);
        double cos = Math.abs(Math.cos(theta));
        double sin = Math.abs(Math.sin(theta));
        double width = original.getWidth();
        double height = original.getHeight();
        int w = (int) (width * cos + height * sin);
        int h = (int) (width * sin + height * cos);

        BufferedImage out = new BufferedImage(w, h, original.getType());
        Graphics2D g2 = out.createGraphics();
        double x = w / 2; //the middle of the two new values
        double y = h / 2;

        AffineTransform at = AffineTransform.getRotateInstance(theta, x, y);
        x = (w - width) / 2;
        y = (h - height) / 2;
        at.translate(x, y);
        g2.drawRenderedImage(original, at);
        g2.dispose();

        return out;
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        if(bi == null)
            return null;

        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
