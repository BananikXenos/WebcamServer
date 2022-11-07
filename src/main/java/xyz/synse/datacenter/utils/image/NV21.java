package xyz.synse.datacenter.utils.image;

import java.awt.image.BufferedImage;

public class NV21 {
	public static BufferedImage plotRGB(int[] rgb, int width, int height) {

		BufferedImage b = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < height; y++) {
			int base = y * width;
			for (int x = 0; x < width; x++) {
				b.setRGB(x, y, rgb[base + x]);
			}
		}

		return b;
	}

	public static BufferedImage YUV_NV21_TO_RGB(ImageData imageData) {
		return YUV_NV21_TO_RGB(imageData.getData(), imageData.getImageWidth(), imageData.getImageHeight());
	}
	public static BufferedImage YUV_NV21_TO_RGB(byte[] yuv, int width, int height) {
		final int frameSize = width * height;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int y = Math.max((0xff & ((int) yuv[i * width + j])), 16);
				int v = (0xff & ((int) yuv[frameSize + (i >> 1) * width + (j & ~1)]));
				int u = (0xff & ((int) yuv[frameSize + (i >> 1) * width + (j & ~1) + 1]));

				int a0 = 1192 * (y - 16);
				int a1 = 1634 * (v - 128);
				int a2 = 832 * (v - 128);
				int a3 = 400 * (u - 128);
				int a4 = 2066 * (u - 128);

				int r = (a0 + a1) >> 10;
				int g = (a0 - a2 - a3) >> 10;
				int b = (a0 + a4) >> 10;

				r = r < 0 ? 0 : (Math.min(r, 255));
				g = g < 0 ? 0 : (Math.min(g, 255));
				b = b < 0 ? 0 : (Math.min(b, 255));

				image.setRGB(j,i, 0xff000000 | (r << 16) | (g << 8) | b);
			}
		}

		return image;
	}
}
