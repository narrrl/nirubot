package nirusu.nirubot.command.fun.moasic.base;

import java.awt.image.BufferedImage;

/**
 * This interface defines the canvas for the art of an {@link IMosaiqueArtist}. On this the artist
 * performs its art.
 *
 * @param <Image>
 *          the type of images the easel will use
 * @author Dominik Fuchss
 *
 */
public interface IMosaiqueEasel<Image extends IArtImage<Image>> {

    /**
     * Create a mosaique for a given image.
     *
     * @param image
     *          the image the artist shall create a mosaique for
     * @param artist
     *          the artist who will create the mosaique
     * @return the mosaique
     * @see IMosaiqueArtist#getTileWidth()
     * @see IMosaiqueArtist#getTileHeight()
     */
    BufferedImage createMosaique(BufferedImage image, IMosaiqueArtist<Image> artist);

}

