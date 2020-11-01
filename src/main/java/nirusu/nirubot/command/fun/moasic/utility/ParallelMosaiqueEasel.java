package nirusu.nirubot.command.fun.moasic.utility;

import nirusu.nirubot.command.fun.moasic.base.BufferedArtImage;
import nirusu.nirubot.command.fun.moasic.base.IMosaiqueArtist;
import nirusu.nirubot.command.fun.moasic.base.IMosaiqueEasel;

import java.awt.image.BufferedImage;

/**
 * this class creates Mosaics parallel but not sequential like {@link nirusu.nirubot.command.fun.moasic.base.IMosaiqueEasel}
 */
public class ParallelMosaiqueEasel implements IMosaiqueEasel<BufferedArtImage> {
    private final int numThreads;

    /**
     * constructor for this class to manually give the number of Threads to use
     *
     * @param numThreads the number of how many Threads to use for creating a mosaic
     */
    public ParallelMosaiqueEasel(final int numThreads) {
        if (numThreads < 0) {
            throw new IllegalArgumentException("invalid amount of provided threads");
        }
        this.numThreads = numThreads;
    }

    /**
     * this constructor automatically selects the number of Threads
     */
    public ParallelMosaiqueEasel() {
        this.numThreads = Runtime.getRuntime().availableProcessors();
    }

    /**
     * creates a mosaic out of the given input with the selected {@link IMosaiqueArtist<BufferedImage>}
     * in a parallel way
     *
     * @param input  the elected Picture
     * @param artist the selected artist for this Picture
     * @return mosaic from the picture
     */
    @Override
    public BufferedImage createMosaique(BufferedImage input, IMosaiqueArtist<BufferedArtImage> artist) {
        BufferedArtImage image = new BufferedArtImage(input);
        BufferedArtImage result = image.createBlankImage();

        //calculate the start and end for each thread
        int[][] matrix = new int[image.getWidth()][image.getHeight()];
        int s = (int) Math.ceil((double) matrix.length / numThreads);
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            int start = i * s;
            int end = Math.min((i + 1) * s, matrix.length);

            Thread thread = new EaselThread(start, end, result, artist, image);
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
        return result.toBufferedImage();
    }
}