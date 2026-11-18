import java.awt.Color;

/** Recolors, rotates (with matrices), and pastes images using 2D pixel arrays. */
public class ImageApp
{
  private static final String LARGE_FILE = "lib/beach.jpg";
  private static final String SMALL_FILE = "lib/flower1.jpg";

  /** Max total RGB difference from the background color to count as background. */
  private static final int BACKGROUND_TOLERANCE = 60;
  /** Share of the small image's color in a blend (0.0 to 1.0). */
  private static final double BLEND_WEIGHT = 0.6;

  public static void main(String[] args)
  {
    Picture origImg = new Picture(LARGE_FILE);
    System.out.println(origImg.getPixels2D()[0][0].getColor());
    show("Original image", origImg);

    // Image #1: change color
    Picture recoloredImg = new Picture(LARGE_FILE);
    changeColor(recoloredImg.getPixels2D(), 60, 0, -60);
    show("Image #1: change color", recoloredImg);

    // Image #2: negative
    Picture negImg = new Picture(LARGE_FILE);
    makeNegative(negImg.getPixels2D());
    show("Image #2: negative", negImg);

    // Image #3: grayscale
    Picture grayscaleImg = new Picture(LARGE_FILE);
    makeGrayscale(grayscaleImg.getPixels2D());
    show("Image #3: grayscale", grayscaleImg);

    // Image #4: rotate 180
    Picture upsideDownImg = rotate(new Picture(LARGE_FILE), Matrix2by2.ROTATE_180);
    show("Image #4: rotate 180", upsideDownImg);

    // Image #5: rotate 90 counterclockwise
    Picture rotateCcwImg =
        rotate(new Picture(LARGE_FILE), Matrix2by2.ROTATE_90_COUNTERCLOCKWISE);
    show("Image #5: rotate 90 counterclockwise", rotateCcwImg);

    // Image #6: rotate 90 clockwise
    Picture rotateCwImg =
        rotate(new Picture(LARGE_FILE), Matrix2by2.ROTATE_90_CLOCKWISE);
    show("Image #6: rotate 90 clockwise", rotateCwImg);

    // Final #7: paste, small colors replace large colors
    Picture pasteReplaceImg = new Picture(LARGE_FILE);
    pasteSmall(new Picture(SMALL_FILE), pasteReplaceImg, 50, 50, false);
    show("Final #7: paste (replace colors)", pasteReplaceImg);

    // Final #8: paste, small colors blend with large colors
    Picture pasteBlendImg = new Picture(LARGE_FILE);
    pasteSmall(new Picture(SMALL_FILE), pasteBlendImg, 50, 50, true);
    show("Final #8: paste (blend colors)", pasteBlendImg);
  }

  /** Prints the label, then opens the image in a window. */
  private static void show(String label, Picture img)
  {
    System.out.println("Showing -> " + label);
    img.explore();
  }

  /** Adds an amount to each color channel of every pixel, limited to 0-255. */
  public static void changeColor(Pixel[][] pixels, int dRed, int dGreen, int dBlue)
  {
    for (int r = 0; r < pixels.length; r++)
    {
      for (int c = 0; c < pixels[r].length; c++)
      {
        Color col = pixels[r][c].getColor();
        pixels[r][c].setColor(new Color(clamp(col.getRed() + dRed),
                                        clamp(col.getGreen() + dGreen),
                                        clamp(col.getBlue() + dBlue)));
      }
    }
  }

  /** Replaces each channel with 255 minus its value. */
  public static void makeNegative(Pixel[][] pixels)
  {
    for (int r = 0; r < pixels.length; r++)
    {
      for (int c = 0; c < pixels[r].length; c++)
      {
        Color col = pixels[r][c].getColor();
        pixels[r][c].setColor(new Color(255 - col.getRed(),
                                        255 - col.getGreen(),
                                        255 - col.getBlue()));
      }
    }
  }

  /** Sets red, green, and blue to the average of the three. */
  public static void makeGrayscale(Pixel[][] pixels)
  {
    for (int r = 0; r < pixels.length; r++)
    {
      for (int c = 0; c < pixels[r].length; c++)
      {
        Color col = pixels[r][c].getColor();
        int avg = (col.getRed() + col.getGreen() + col.getBlue()) / 3;
        pixels[r][c].setColor(new Color(avg, avg, avg));
      }
    }
  }

  /** Limits a value to 0-255. */
  private static int clamp(int value)
  {
    return Math.max(0, Math.min(255, value));
  }

  /**
   * Returns a new Picture with every pixel's (row, col) multiplied by the
   * matrix, then shifted so the smallest row and column are 0.
   */
  public static Picture rotate(Picture src, Matrix2by2 m)
  {
    Pixel[][] in = src.getPixels2D();
    int rows = in.length;
    int cols = in[0].length;

    // Transform the corners to find the new image size
    Vector1by2[] corners = {
        new Vector1by2(0, 0),
        new Vector1by2(0, cols - 1),
        new Vector1by2(rows - 1, 0),
        new Vector1by2(rows - 1, cols - 1) };

    double minRow = Double.MAX_VALUE, maxRow = -Double.MAX_VALUE;
    double minCol = Double.MAX_VALUE, maxCol = -Double.MAX_VALUE;
    for (Vector1by2 corner : corners)
    {
      Vector1by2 t = Matrix2by2.multiply(corner, m);
      minRow = Math.min(minRow, t.getRow());
      maxRow = Math.max(maxRow, t.getRow());
      minCol = Math.min(minCol, t.getCol());
      maxCol = Math.max(maxCol, t.getCol());
    }

    int newRows = (int) Math.round(maxRow - minRow) + 1;
    int newCols = (int) Math.round(maxCol - minCol) + 1;
    Picture dest = new Picture(newRows, newCols);
    Pixel[][] out = dest.getPixels2D();

    for (int r = 0; r < rows; r++)
    {
      for (int c = 0; c < cols; c++)
      {
        Vector1by2 t = Matrix2by2.multiply(new Vector1by2(r, c), m);
        int newR = (int) Math.round(t.getRow() - minRow);
        int newC = (int) Math.round(t.getCol() - minCol);
        out[newR][newC].setColor(in[r][c].getColor());
      }
    }
    return dest;
  }

  /**
   * Pastes small onto large at (startRow, startCol), skipping pixels near the
   * small image's top-left color. If blend is true, mixes with the large
   * image's color instead of replacing it.
   */
  public static void pasteSmall(Picture small, Picture large,
                                int startRow, int startCol, boolean blend)
  {
    Pixel[][] smallPixels = small.getPixels2D();
    Pixel[][] largePixels = large.getPixels2D();
    Color background = smallPixels[0][0].getColor();

    for (int r = 0; r < smallPixels.length; r++)
    {
      for (int c = 0; c < smallPixels[r].length; c++)
      {
        int destR = startRow + r;
        int destC = startCol + c;
        boolean onLarge = destR < largePixels.length
                       && destC < largePixels[0].length;
        Color smallColor = smallPixels[r][c].getColor();

        if (onLarge && !isBackground(smallColor, background))
        {
          if (blend)
            smallColor = blendColors(smallColor,
                                     largePixels[destR][destC].getColor(),
                                     BLEND_WEIGHT);
          largePixels[destR][destC].setColor(smallColor);
        }
      }
    }
  }

  /** True if the color is within the tolerance of the background color. */
  private static boolean isBackground(Color color, Color background)
  {
    int difference = Math.abs(color.getRed() - background.getRed())
                   + Math.abs(color.getGreen() - background.getGreen())
                   + Math.abs(color.getBlue() - background.getBlue());
    return difference < BACKGROUND_TOLERANCE;
  }

  /** Mixes two colors; weight is the share of the top color. */
  private static Color blendColors(Color top, Color bottom, double weight)
  {
    int red   = (int) (weight * top.getRed()   + (1 - weight) * bottom.getRed());
    int green = (int) (weight * top.getGreen() + (1 - weight) * bottom.getGreen());
    int blue  = (int) (weight * top.getBlue()  + (1 - weight) * bottom.getBlue());
    return new Color(red, green, blue);
  }
}
