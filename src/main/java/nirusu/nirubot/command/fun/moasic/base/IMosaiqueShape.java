package nirusu.nirubot.command.fun.moasic.base;

import java.awt.image.BufferedImage;

/**
 * This interface defines a shape for a mosaique. A tile of a mosaique consists of at least one
 * shape. The {@link IMosaiqueArtist} uses the {@link IMosaiqueShape IMosaiqueShapes} to create a
 * mosaique.
 *
 * @author Dominik Fuchss
 *
 * @param <Image>
 *          the type of image to use
 */
public interface IMosaiqueShape<Image extends IArtImage<Image>> {

    /**
     * Creates a thumbnail of the shape. The thumbnail represents the shape as an
     * {@link BufferedImage} of the size defined by {@link #getWidth()} and {@link #getHeight()}.
     *
     * @return the thumbnail
     */
    BufferedImage getThumbnail();

    /**
     * Calculate the average color of the shape.
     *
     * @return the average color as ARGB
     */
    int getAverageColor();

    /**
     * Request the shape to be drawn to a specific region.
     *
     * @param targetRegion
     *          the region on which the shape shall be drawn
     * @throws IllegalArgumentException
     *           iff targetRegion does not match width and height
     */
    void drawMe(Image targetRegion);

    /**
     * Get the width of the shape.
     *
     * @return the width
     */
    int getWidth();

    /**
     * Get the height of the shape.
     *
     * @return the height
     */
    int getHeight();

}

