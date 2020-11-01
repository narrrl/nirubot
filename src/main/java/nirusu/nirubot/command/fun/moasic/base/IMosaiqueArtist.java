package nirusu.nirubot.command.fun.moasic.base;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Define an artist who can create a tiling for a small region.<br>
 * The artist chooses a suitable tile for the region the artist has to work on.
 *
 * @author Dominik Fuchss
 *
 * @param <Image>
 *          the type of image suitable for the artist
 */
public interface IMosaiqueArtist<Image extends IArtImage<Image>> {
    /**
     * Get the width of tiles supported by the {@link IMosaiqueArtist}.
     *
     * @return the width of tiles
     */
    int getTileWidth();

    /**
     * Get the height of tiles supported by the {@link IMosaiqueArtist}.
     *
     * @return the height of tiles
     */
    int getTileHeight();

    /**
     * Create thumbnails of the usable tiles of the artist.
     *
     * @return a list of thumbnails
     * @see IMosaiqueShape#getThumbnail()
     */
    List<BufferedImage> getThumbnails();

    /**
     * The artist works on a small region and chooses the most fitting tile for the given region.<br>
     * Iff the region is smaller than {@link #getTileWidth()} and/or {@link #getTileHeight()} the
     * artist will choose the matching tiles anyway. However, the respective dimensions are cut off.
     *
     * @param region
     *          the region which needs a tiling
     * @return the tiling as an image
     * @throws IllegalArgumentException
     *           iff requested tiling is greater than {@link #getTileWidth() width of tiles} or
     *           {@link #getTileHeight() height of tiles}
     */
    Image getTileForRegion(Image region);

}

