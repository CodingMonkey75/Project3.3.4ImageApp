/** An immutable 1x2 row vector holding a pixel location as (row, col). */
public class Vector1by2
{
  /** Values closer than this are treated as equal. */
  public static final double EPSILON = 1e-9;

  private final double row;
  private final double col;

  /** Creates a vector from a row and column value. */
  public Vector1by2(double row, double col)
  {
    this.row = row;
    this.col = col;
  }

  /** Returns the row component. */
  public double getRow()
  {
    return row;
  }

  /** Returns the column component. */
  public double getCol()
  {
    return col;
  }

  /** Returns a.row*b.row + a.col*b.col. */
  public static double dot(Vector1by2 a, Vector1by2 b)
  {
    return a.row * b.row + a.col * b.col;
  }

  /** Returns a + b. */
  public static Vector1by2 add(Vector1by2 a, Vector1by2 b)
  {
    return new Vector1by2(a.row + b.row, a.col + b.col);
  }

  /** Returns a - b. */
  public static Vector1by2 subtract(Vector1by2 a, Vector1by2 b)
  {
    return new Vector1by2(a.row - b.row, a.col - b.col);
  }

  /** Returns k * v. */
  public static Vector1by2 scale(Vector1by2 v, double k)
  {
    return new Vector1by2(k * v.row, k * v.col);
  }

  /** True if both components match within EPSILON. */
  public static boolean approxEquals(Vector1by2 a, Vector1by2 b)
  {
    return Math.abs(a.row - b.row) < EPSILON
        && Math.abs(a.col - b.col) < EPSILON;
  }

  @Override
  public String toString()
  {
    return "[" + row + " " + col + "]";
  }
}
