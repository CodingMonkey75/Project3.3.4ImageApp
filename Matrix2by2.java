/**
 * An immutable 2x2 matrix for transforming (row, col) row vectors:
 * [row col] * M.
 */
public class Matrix2by2
{
  public static final Matrix2by2 IDENTITY = new Matrix2by2(1, 0,
                                                           0, 1);

  /** (r, c) -> (-r, -c). */
  public static final Matrix2by2 ROTATE_180 = new Matrix2by2(-1,  0,
                                                              0, -1);

  /** (r, c) -> (c, -r): 90 degrees clockwise on screen. */
  public static final Matrix2by2 ROTATE_90_CLOCKWISE = new Matrix2by2( 0, -1,
                                                                       1,  0);

  /** (r, c) -> (-c, r): 90 degrees counterclockwise on screen. */
  public static final Matrix2by2 ROTATE_90_COUNTERCLOCKWISE =
      new Matrix2by2(0, 1,
                    -1, 0);

  private final double m00, m01;
  private final double m10, m11;

  /** Creates a matrix from its entries in reading order. */
  public Matrix2by2(double m00, double m01, double m10, double m11)
  {
    this.m00 = m00;
    this.m01 = m01;
    this.m10 = m10;
    this.m11 = m11;
  }

  /** Returns row i (0 or 1) as a vector. */
  public Vector1by2 getRowVector(int i)
  {
    return (i == 0) ? new Vector1by2(m00, m01) : new Vector1by2(m10, m11);
  }

  /** Returns column j (0 or 1) as a vector. */
  public Vector1by2 getColumnVector(int j)
  {
    return (j == 0) ? new Vector1by2(m00, m10) : new Vector1by2(m01, m11);
  }

  /** Returns the entry at row i, column j. */
  public double get(int i, int j)
  {
    return (i == 0) ? ((j == 0) ? m00 : m01) : ((j == 0) ? m10 : m11);
  }

  /** Returns v * m: each result is the dot product of v with a column of m. */
  public static Vector1by2 multiply(Vector1by2 v, Matrix2by2 m)
  {
    return new Vector1by2(Vector1by2.dot(v, m.getColumnVector(0)),
                          Vector1by2.dot(v, m.getColumnVector(1)));
  }

  /** Returns a * b: entry (i, j) is row i of a dotted with column j of b. */
  public static Matrix2by2 multiply(Matrix2by2 a, Matrix2by2 b)
  {
    return new Matrix2by2(
        Vector1by2.dot(a.getRowVector(0), b.getColumnVector(0)),
        Vector1by2.dot(a.getRowVector(0), b.getColumnVector(1)),
        Vector1by2.dot(a.getRowVector(1), b.getColumnVector(0)),
        Vector1by2.dot(a.getRowVector(1), b.getColumnVector(1)));
  }

  /** Returns a + b. */
  public static Matrix2by2 add(Matrix2by2 a, Matrix2by2 b)
  {
    return new Matrix2by2(a.m00 + b.m00, a.m01 + b.m01,
                          a.m10 + b.m10, a.m11 + b.m11);
  }

  /** Returns k * m. */
  public static Matrix2by2 scale(Matrix2by2 m, double k)
  {
    return new Matrix2by2(k * m.m00, k * m.m01,
                          k * m.m10, k * m.m11);
  }

  /** Returns the transpose of m. */
  public static Matrix2by2 transpose(Matrix2by2 m)
  {
    return new Matrix2by2(m.m00, m.m10,
                          m.m01, m.m11);
  }

  /** Returns m00*m11 - m01*m10. */
  public static double determinant(Matrix2by2 m)
  {
    return m.m00 * m.m11 - m.m01 * m.m10;
  }

  /** True if every entry matches within EPSILON. */
  public static boolean approxEquals(Matrix2by2 a, Matrix2by2 b)
  {
    return Vector1by2.approxEquals(a.getRowVector(0), b.getRowVector(0))
        && Vector1by2.approxEquals(a.getRowVector(1), b.getRowVector(1));
  }

  @Override
  public String toString()
  {
    return "[" + getRowVector(0) + " " + getRowVector(1) + "]";
  }
}