package nirusu.nirubot.command.fun.moasic.utility;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * this class is a thread used in {@link ParallelRectangleArtist}.
 * its used to crate a number of {@link ParallelRectangleShape} and add them parallel to the artist.
 */
public class ArtistThread extends Thread {
    private final List<ParallelRectangleShape> shapes;
    private final List<BufferedImage> images;
    private final int tileWidth;
    private final int tileHeight;
    private final int start;
    private final int end;

    /**
     * Constructor for this thread
     *
     * @param shapes     the list where the shapes should be added
     * @param images     the given images
     * @param tileWidth  the tile width of the new shapes
     * @param tileHeight the tile height of the new shapes
     * @param start      signals this thread where to start working
     * @param end        signals the thread where to stop working
     */
    public ArtistThread(List<ParallelRectangleShape> shapes, List<BufferedImage> images,
                        int tileWidth, int tileHeight, int start, int end) {
        this.shapes = shapes;
        this.images = images;
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            ParallelRectangleShape shape = new ParallelRectangleShape(images.get(i), tileWidth, tileHeight);
            synchronized (shapes) {
                shapes.add(shape);
            }
        }
    }
}
