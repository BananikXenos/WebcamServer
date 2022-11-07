package xyz.synse.datacenter.utils.image;

public class ImageData {
    private final byte[] data;
    private final int imageWidth;
    private final int imageHeight;

    public ImageData(byte[] data, int imageWidth, int imageHeight) {
        this.data = data;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public byte[] getData() {
        return data;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }
}
