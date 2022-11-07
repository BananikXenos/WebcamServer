package xyz.synse.datacenter.utils.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class IconUtils {
    public static BufferedImage warningLogo = loadImage("/Icons/warning.png");

    public static BufferedImage loadImage(String localPath){
        try {
            return ImageIO.read(IconUtils.class.getResourceAsStream(localPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
