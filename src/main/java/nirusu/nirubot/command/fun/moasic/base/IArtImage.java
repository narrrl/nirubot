package nirusu.nirubot.command.fun.moasic.base;

import java.awt.image.BufferedImage;

/**
 * The <b>artistic image</b>. This interface represents an image for the mosaique project.
 *
 * @author Dominik Fuchss
 *
 * @param <Image>
 *          used as generic finalization;
 */
public interface IArtImage<Image extends IArtImage<Image>> {
    /**
     * Create a new transparent image with the same dimensions as this.
     *
     * @return a new transparent image
     */
    Image createBlankImage();

    /**
     * Create a new {@link BufferedImage} from the current state of the {@link IArtImage}.
     *
     * @return a new {@link BufferedImage}
     */
    BufferedImage toBufferedImage();

    /**
     * Get a subimage of the current image. Changes in the subimage could impact this image.
     *
     * @param x
     *          the x coordinate to start
     * @param y
     *          the y coordinate to start
     * @param width
     *          the width of the subimage
     * @param height
     *          the height of the subimage
     * @return the subimage
     */
    Image getSubimage(int x, int y, int width, int height);

    /**
     * Set a subimage of the current image at a specific position to the specified image.
     *
     * @param x
     *          the x coordinate to start
     * @param y
     *          the y coordinate to start
     * @param image
     *          the subimage to use
     */
    void setSubimage(int x, int y, Image image);

    /**
     * Get the color of a specific pixel.
     *
     * @param x
     *          the x coordinate
     * @param y
     *          the y coordinate
     * @return the color of the specific pixel as ARGB color
     */
    int getRGB(int x, int y);

    /**
     * Set the color of a specific pixel.
     *
     * @param x
     *          the x coordinate
     * @param y
     *          the y coordinate
     * @param rgb
     *          the color as ARGB color
     */
    void setRGB(int x, int y, int rgb);

    /**
     * Get the width of the image.
     *
     * @return the width
     */
    int getWidth();

    /**
     * Get the height of the image.
     *
     * @return the height
     */
    int getHeight();

}

