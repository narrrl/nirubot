package nirusu.nirubot.command.fun.moasic.base;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;


/**
 * Some helper methods to work with images.
 *
 * @author Dominik Fuchss
 *
 */
public final class ImageUtils {
    private ImageUtils() {
        throw new IllegalAccessError();
    }

    /**
     * Scale Image by width and height. The image will be scaled and then cropped to the requested
     * size iff needed.
     *
     * @param input
     *          image to scale
     * @param width
     *          the target width
     * @param height
     *          the target height
     * @return scaled and cropped image
     * @throws IllegalArgumentException
     *           iff width or height are not suitable
     */
    public static BufferedImage scaleAndCrop(BufferedImage input, int width, int height) {
        float factorX = 1F * width / input.getWidth();
        float factorY = 1F * height / input.getHeight();

        BufferedImage scaled = factorX > factorY //
                ? scaleWidth(input, width)
                : scaleHeight(input, height);

        int scaleW = scaled.getWidth();
        int scaleH = scaled.getHeight();

        BufferedImage cropped = //
                scaled.getSubimage((scaleW - width) / 2, (scaleH - height) / 2, width, height);

        return cropped;
    }

    /**
     * Scale Image by width (set width and the height will be calculated).
     *
     * @param input
     *          image to scale
     *
     * @param width
     *          the target width
     *
     * @return scaled image
     * @throws IllegalArgumentException
     *           iff width or height are not suitable (given or calculated)
     */
    public static BufferedImage scaleWidth(BufferedImage input, int width) {
        if (width <= 0) {
            throw new IllegalArgumentException("width cannot be <= 0");
        }
        Image scaled = input.getScaledInstance(width, -1, Image.SCALE_SMOOTH);
        int height = scaled.getHeight(null);
        if (height <= 0) {
            throw new IllegalArgumentException("height would be 0");
        }
        BufferedImage res = new BufferedImage(width, height, input.getType());
        Graphics2D g2d = res.createGraphics();
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();
        res.flush();
        return res;
    }

    /**
     * Scale Image by height (set height and the width will be calculated).
     *
     * @param input
     *          image to scale
     *
     * @param height
     *          the target width
     *
     * @return scaled image
     * @throws IllegalArgumentException
     *           iff width or height are not suitable (given or calculated)
     */
    public static BufferedImage scaleHeight(BufferedImage input, int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("width cannot be <= 0");
        }
        Image scaled = input.getScaledInstance(-1, height, Image.SCALE_SMOOTH);
        int width = scaled.getWidth(null);
        if (width <= 0) {
            throw new IllegalArgumentException("width would be 0");
        }
        BufferedImage res = new BufferedImage(width, height, input.getType());
        Graphics2D g2d = res.createGraphics();
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();
        res.flush();
        return res;
    }

}

