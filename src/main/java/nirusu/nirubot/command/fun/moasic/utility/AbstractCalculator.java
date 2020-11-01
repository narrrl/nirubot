package nirusu.nirubot.command.fun.moasic.utility;

import nirusu.nirubot.command.fun.moasic.base.BufferedArtImage;
import nirusu.nirubot.command.fun.moasic.base.IMosaiqueArtist;
import nirusu.nirubot.command.fun.moasic.base.IMosaiqueShape;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;

/**
 * The abstract base class for all calculators for {@link IMosaiqueArtist} and
 * {@link IMosaiqueShape}.
 *
 * @author Dominik Fuchss
 *
 */
public abstract class AbstractCalculator {

  /**
   * Calculate a specific average color for a given region.
   *
   * @param region
   *          the region
   * @return the average color as ARGB
   */
  public final int averageColor(BufferedImage region) {
    long r = 0;
    long g = 0;
    long b = 0;
    long a = 0;
    int ctr = 0;

    for (int x = 0; x < region.getWidth(); x++) {
      var yIter = getIteratorForColumn(region.getWidth(), region.getHeight(), x);
      while (yIter.hasNext()) {
        int y = yIter.next();
        int col = region.getRGB(x, y);

        Color c = new Color(col, true);
        r += c.getRed();
        g += c.getGreen();
        b += c.getBlue();
        a += c.getAlpha();
        ctr++;
      }
    }
    return new Color((int) (r / ctr), (int) (g / ctr), (int) (b / ctr), (int) (a / ctr)).getRGB();
  }

  /**
   * Draw the shape of the specific rectangle based on the target region and the scaled instance.
   *
   * @param src
   *          the source image
   * @param dest
   *          the target region
   * @param w
   *          the width to be drawn
   * @param h
   *          the height to be drawn
   */
  public final void applyTiling(BufferedImage src, BufferedArtImage dest, int w, int h) {
    for (int x = 0; x < w; x++) {
      var yIter = getIteratorForColumn(w, h, x);
      while (yIter.hasNext()) {
        int y = yIter.next();
        dest.setRGB(x, y, src.getRGB(x, y));
      }
    }
  }

  /**
   * Get an iterator over all y values of a given column x.
   * 
   * @param w
   *          the width of the overall region
   * @param h
   *          the height of the overall region
   * @param x
   *          the current column
   * @return an iterator over y values
   */
  protected abstract Iterator<Integer> getIteratorForColumn(int w, int h, int x);

}