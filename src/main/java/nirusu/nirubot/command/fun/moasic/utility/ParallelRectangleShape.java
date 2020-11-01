package nirusu.nirubot.command.fun.moasic.utility;

import nirusu.nirubot.command.fun.moasic.base.BufferedArtImage;
import nirusu.nirubot.command.fun.moasic.base.IMosaiqueShape;

import java.awt.image.BufferedImage;

/**
 * this class is a RectangleShape that uses {@link BufferedImage} instead of
 * {@link BufferedArtImage}.
 */
public final class ParallelRectangleShape extends AbstractShape {

    /**
     * Create a new {@link IMosaiqueShape} by image.
     *
     * @param image the image to use
     * @param w     the width
     * @param h     the height
     */
    protected ParallelRectangleShape(BufferedImage image, int w, int h) {
        super(new BufferedArtImage(image), w, h);
    }

    @Override
    protected AbstractCalculator getCalculator() {
        return RectangleCalculator.getInstance();
    }
}