package nirusu.nirubot.command.fun.moasic.base;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

/**
 * A simple implementation of {@link IArtImage} which uses {@link BufferedImage BufferedImages}
 *
 * @author Dominik Fuchss
 *
 */
public class BufferedArtImage implements IArtImage<BufferedArtImage> {
    private final BufferedImage image;

    /**
     * Create a new transparent image.
     *
     * @param width
     *          the width
     * @param height
     *          the height
     */
    public BufferedArtImage(int width, int height) {
        var image = new BufferedImage(width, height, TYPE_INT_ARGB);
        int[] transparent = new int[width * height * 4];
        image.setRGB(0, 0, width, height, transparent, 0, 4);
        this.image = image;
    }

    /**
     * Create a new {@link BufferedArtImage} by the data of a {@link BufferedImage}.
     *
     * @param image
     *          the source image
     */
    public BufferedArtImage(BufferedImage image) {
        if (image.getType() == TYPE_INT_ARGB) {
            this.image = image;
        } else {
            BufferedImage argb = new BufferedImage(image.getWidth(), image.getHeight(), TYPE_INT_ARGB);
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    argb.setRGB(x, y, image.getRGB(x, y));
                }
            }
            this.image = argb;
        }
    }

    @Override
    public BufferedArtImage createBlankImage() {
        return new BufferedArtImage(this.getWidth(), this.getHeight());
    }

    @Override
    public BufferedImage toBufferedImage() {
        return this.image;
    }

    @Override
    public BufferedArtImage getSubimage(int x, int y, int width, int height) {
        try {
            var rect = image.getSubimage(x, y, width, height);
            return new BufferedArtImage(rect);
        } catch (RasterFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void setSubimage(int x, int y, BufferedArtImage rectangle) {
        this.image.getGraphics().drawImage(rectangle.image, x, y, null);
    }

    @Override
    public int getRGB(int x, int y) {
        return this.image.getRGB(x, y);
    }

    @Override
    public void setRGB(int x, int y, int rgb) {
        this.image.setRGB(x, y, rgb);
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

}