package nirusu.nirubot.command.fun.moasic.utility;

import nirusu.nirubot.command.fun.moasic.base.BufferedArtImage;
import nirusu.nirubot.command.fun.moasic.base.IMosaiqueShape;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * this class creates {@link ParallelRectangleShape} in a parallel way.
 * it works the same way as RectangleShape just faster.
 * this class uses {@link BufferedImage} instead of {@link BufferedArtImage} for a faster process.
 */
public class ParallelRectangleArtist extends AbstractArtist {
    private final List<BufferedImage> images;
    private final int tileWidth;
    private final int tileHeight;
    private final List<ParallelRectangleShape> shapes;

    /**
     * the constructor of this class
     *
     * @param images     the provided images
     * @param tileWidth  the tile width of the new shapes
     * @param tileHeight the tile height of the new shapes
     * @param numThreads the number of threads u want to use
     */
    public ParallelRectangleArtist(List<BufferedImage> images, int tileWidth, int tileHeight, int numThreads) {
        super(tileWidth, tileHeight);
        if (numThreads < 0) {
            throw new IllegalArgumentException("invalid amount of provided threads");
        }
        if (images.isEmpty()) {
            throw new IllegalArgumentException("no tiles provided");
        }
        this.images = images;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.shapes = new ArrayList<>();

        makeShapes(numThreads);

    }

    /**
     * the constructor of this class
     * automatically selects the given amount of threads. (the processor amount)
     *
     * @param images     the provided images
     * @param tileWidth  the tile width of the new shapes
     * @param tileHeight the tile height of the new shapes
     */
    public ParallelRectangleArtist(List<BufferedImage> images, final int tileWidth, final int tileHeight) {
        super(tileWidth, tileHeight);
        if (images.isEmpty()) {
            throw new IllegalArgumentException("no tiles provided");
        }
        this.images = images;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.shapes = new ArrayList<>();

        makeShapes(Runtime.getRuntime().availableProcessors());
    }

    /**
     * creates the new shapes and adds them to the list shapes in a parallel way.
     *
     * @param numThreads the number of threads you want to use for this process.
     */
    private void makeShapes(int numThreads) {
        int s = (int) Math.ceil((double) images.size() / numThreads);
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            int start = i * s;
            int end = Math.min((i + 1) * s, images.size());

            Thread thread = new ArtistThread(shapes, images, tileWidth, tileHeight, start, end);
            threads[i] = thread;
            thread.start();
        }
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<BufferedImage> getThumbnails() {
        return shapes.stream().map(ParallelRectangleShape::getThumbnail).collect(Collectors.toList());
    }

    @Override
    protected void drawTileForRegion(BufferedImage region, BufferedArtImage target) {
        int average = RectangleCalculator.getInstance().averageColor(region);
        IMosaiqueShape<BufferedArtImage> tile = findNearest(average, shapes);
        tile.drawMe(target);
    }
}
