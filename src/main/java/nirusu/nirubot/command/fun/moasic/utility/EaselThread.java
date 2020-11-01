package nirusu.nirubot.command.fun.moasic.utility;

import nirusu.nirubot.command.fun.moasic.base.BufferedArtImage;
import nirusu.nirubot.command.fun.moasic.base.IMosaiqueArtist;


/**
 * this class is thread used in {@link ParallelMosaiqueEasel}.
 * its used to create mosaics parallel.
 */
public class EaselThread extends Thread {
    private final int start;
    private final int end;
    private final BufferedArtImage image;
    private final int tileWidth;
    private final int tileHeight;
    private final IMosaiqueArtist<BufferedArtImage> artist;
    private final BufferedArtImage result;

    /**
     * the constructor for this thread
     *
     * @param start  signals the thread where to start in the matrix
     * @param end    signals the thread where to stop in the matrix
     * @param result the mosaic u are editing
     * @param artist the artist to create the mosaic
     * @param image  the input image
     */
    public EaselThread(int start, int end, BufferedArtImage result,
                       IMosaiqueArtist<BufferedArtImage> artist, BufferedArtImage image) {
        this.tileWidth = artist.getTileWidth();
        this.tileHeight = artist.getTileHeight();
        this.start = start;
        this.end = end;
        this.result = result;
        this.artist = artist;
        this.image = image;
    }

    @Override
    public synchronized void run() {
        for (int x = start; x < end; x += tileWidth) {
            for (int y = 0; y < image.getHeight(); y += tileHeight) {
                int width = x + tileWidth < image.getWidth() ? tileWidth : image.getWidth() - x;
                int height = y + tileHeight < image.getHeight() ? tileHeight : image.getHeight() - y;

                BufferedArtImage sub = image.getSubimage(x, y, width, height);
                BufferedArtImage tile = artist.getTileForRegion(sub);

                result.setSubimage(x, y, tile);
            }
        }
    }
}
