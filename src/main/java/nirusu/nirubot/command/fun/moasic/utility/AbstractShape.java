package nirusu.nirubot.command.fun.moasic.utility;


import nirusu.nirubot.command.fun.moasic.base.BufferedArtImage;
import nirusu.nirubot.command.fun.moasic.base.IMosaiqueShape;
import nirusu.nirubot.command.fun.moasic.base.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.Objects;

public abstract class AbstractShape implements IMosaiqueShape<BufferedArtImage> {
  protected final BufferedImage image;
  private AbstractCalculator calc;

  private int average;

  /**
   * Create a new {@link IMosaiqueShape} by image.
   *
   * @param image
   *          the image to use
   * @param w
   *          the width
   * @param h
   *          the height
   */
  protected AbstractShape(BufferedArtImage image, int w, int h) {
    this.image = ImageUtils.scaleAndCrop(Objects.requireNonNull(image.toBufferedImage()), w, h);
    this.calc = getCalculator();
    this.average = this.calc.averageColor(this.image);
  }

  /**
   * Get the calculator for the shape.
   *
   * @return the calculator
   */
  protected abstract AbstractCalculator getCalculator();

  @Override
  public final int getAverageColor() {
    return average;
  }

  @Override
  public BufferedImage getThumbnail() {
    BufferedArtImage res = new BufferedArtImage(image.getWidth(), image.getHeight());
    this.drawMe(res);
    return res.toBufferedImage();
  }

  @Override
  public final void drawMe(BufferedArtImage targetRect) {
    if (targetRect.getWidth() > this.getWidth() || targetRect.getHeight() > this.getHeight()) {
      throw new IllegalArgumentException("dimensions of target are too big for this tile");
    }

    int w = Math.min(this.getWidth(), targetRect.getWidth());
    int h = Math.min(this.getHeight(), targetRect.getHeight());

    this.calc.applyTiling(image, targetRect, w, h);
  }

  @Override
  public final int getWidth() {
    return image.getWidth();
  }

  @Override
  public final int getHeight() {
    return image.getHeight();
  }
}
