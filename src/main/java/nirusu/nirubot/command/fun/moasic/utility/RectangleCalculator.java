package nirusu.nirubot.command.fun.moasic.utility;

import java.util.Iterator;
import java.util.stream.IntStream;

/**
 * Helper class for the RectangleArtist and RectangleShape.
 *
 * @author Dominik Fuchss
 *
 */
public final class RectangleCalculator extends AbstractCalculator {
  private static RectangleCalculator instance;

  /**
   * Get the one and only instance of the calculator.
   *
   * @return the instance
   */
  public static RectangleCalculator getInstance() {
    if (instance == null) {
      instance = new RectangleCalculator();
    }
    return instance;
  }

  private RectangleCalculator() {
  }

  @Override
  protected Iterator<Integer> getIteratorForColumn(int w, int h, int x) {
    return IntStream.range(0, h).iterator();
  }

}
