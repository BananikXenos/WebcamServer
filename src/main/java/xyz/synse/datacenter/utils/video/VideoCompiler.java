package xyz.synse.datacenter.utils.video;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.Codec;
import org.jcodec.common.Format;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Rational;
import org.jcodec.scale.AWTUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class VideoCompiler {
    private final SequenceEncoder mEncoder;

    public VideoCompiler(File file, int fps) throws IOException {
        this.mEncoder = new SequenceEncoder(NIOUtils.writableChannel(file),
                Rational.R(fps, 1), Format.MOV, Codec.H264, null);
    }

    public void appendImage(BufferedImage image) throws IOException {
        mEncoder.encodeNativeFrame(AWTUtil.fromBufferedImageRGB(image));
    }

    public void finish() throws IOException {
        mEncoder.finish();
    }
}
