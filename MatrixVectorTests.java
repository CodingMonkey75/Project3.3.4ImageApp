/** Unit tests for Vector1by2 and Matrix2by2. Run: java MatrixVectorTests */
public class MatrixVectorTests
{
  private static int passed = 0;
  private static int failed = 0;

  public static void main(String[] args)
  {
    System.out.println("=== Vector1by2 public interface ===");
    testConstructorAndGetters();
    testAdd();
    testSubtract();
    testScale();
    testApproxEquals();
    testVectorToString();

    System.out.println("\n=== dot(Vector1by2, Vector1by2) ===");
    testDot();

    System.out.println("\n=== Matrix2by2 public interface ===");
    testMatrixGetters();
    testMatrixMultiplyMatrix();
    testMatrixAddScaleTranspose();
    testDeterminant();
    testMatrixApproxEquals();
    testMatrixToString();

    System.out.println("\n=== multiply(Vector1by2, Matrix2by2) ===");
    testVectorTimesMatrix();
    testRotationMatrices();

    System.out.println("\nPassed: " + passed + "   Failed: " + failed);
    System.exit(failed == 0 ? 0 : 1);
  }

  /** Counts and prints one pass/fail result. */
  private static void check(String name, boolean condition)
  {
    if (condition)
      passed++;
    else
      failed++;
    System.out.println((condition ? "  PASS  " : "  FAIL  ") + name);
  }

  private static Vector1by2 v(double r, double c)
  {
    return new Vector1by2(r, c);
  }

  private static void testConstructorAndGetters()
  {
    Vector1by2 a = v(3, 4);
    check("getRow() returns 3", a.getRow() == 3.0);
    check("getCol() returns 4", a.getCol() == 4.0);
  }

  private static void testAdd()
  {
    check("[1 2] + [3 4] = [4 6]",
          Vector1by2.approxEquals(Vector1by2.add(v(1, 2), v(3, 4)), v(4, 6)));
  }

  private static void testSubtract()
  {
    check("[5 7] - [2 3] = [3 4]",
          Vector1by2.approxEquals(Vector1by2.subtract(v(5, 7), v(2, 3)), v(3, 4)));
  }

  private static void testScale()
  {
    check("2 * [3 -4] = [6 -8]",
          Vector1by2.approxEquals(Vector1by2.scale(v(3, -4), 2), v(6, -8)));
  }

  private static void testApproxEquals()
  {
    check("approxEquals: same values", Vector1by2.approxEquals(v(1, 2), v(1, 2)));
    check("approxEquals: tiny round-off", Vector1by2.approxEquals(v(0.1 + 0.2, 1), v(0.3, 1)));
    check("approxEquals: different values", !Vector1by2.approxEquals(v(1, 2), v(1, 3)));
  }

  private static void testVectorToString()
  {
    check("toString() is [1.0 2.0]", v(1, 2).toString().equals("[1.0 2.0]"));
  }

  private static void testDot()
  {
    check("dot([1 2],[3 4]) = 11", Vector1by2.dot(v(1, 2), v(3, 4)) == 11.0);
    check("dot([1 0],[0 1]) = 0 (perpendicular)", Vector1by2.dot(v(1, 0), v(0, 1)) == 0.0);
    check("dot([-2 5],[4 1]) = -3", Vector1by2.dot(v(-2, 5), v(4, 1)) == -3.0);
  }

  private static void testMatrixGetters()
  {
    Matrix2by2 m = new Matrix2by2(1, 2, 3, 4);
    check("get(0,1) = 2", m.get(0, 1) == 2.0);
    check("get(1,0) = 3", m.get(1, 0) == 3.0);
    check("row 1 = [3 4]", Vector1by2.approxEquals(m.getRowVector(1), v(3, 4)));
    check("column 0 = [1 3]", Vector1by2.approxEquals(m.getColumnVector(0), v(1, 3)));
    check("column 1 = [2 4]", Vector1by2.approxEquals(m.getColumnVector(1), v(2, 4)));
  }

  private static void testMatrixMultiplyMatrix()
  {
    Matrix2by2 a = new Matrix2by2(1, 2, 3, 4);
    Matrix2by2 b = new Matrix2by2(5, 6, 7, 8);
    Matrix2by2 expected = new Matrix2by2(19, 22, 43, 50);
    check("[[1 2][3 4]] * [[5 6][7 8]] = [[19 22][43 50]]",
          Matrix2by2.approxEquals(Matrix2by2.multiply(a, b), expected));
    check("A * IDENTITY = A",
          Matrix2by2.approxEquals(Matrix2by2.multiply(a, Matrix2by2.IDENTITY), a));
    check("ROTATE_90_CW * ROTATE_90_CW = ROTATE_180",
          Matrix2by2.approxEquals(Matrix2by2.multiply(Matrix2by2.ROTATE_90_CLOCKWISE,
                                                      Matrix2by2.ROTATE_90_CLOCKWISE),
                                  Matrix2by2.ROTATE_180));
    check("ROTATE_90_CW * ROTATE_90_CCW = IDENTITY",
          Matrix2by2.approxEquals(Matrix2by2.multiply(Matrix2by2.ROTATE_90_CLOCKWISE,
                                                      Matrix2by2.ROTATE_90_COUNTERCLOCKWISE),
                                  Matrix2by2.IDENTITY));
  }

  private static void testMatrixAddScaleTranspose()
  {
    Matrix2by2 a = new Matrix2by2(1, 2, 3, 4);
    Matrix2by2 b = new Matrix2by2(5, 6, 7, 8);
    check("add = [[6 8][10 12]]",
          Matrix2by2.approxEquals(Matrix2by2.add(a, b), new Matrix2by2(6, 8, 10, 12)));
    check("scale by 3 = [[3 6][9 12]]",
          Matrix2by2.approxEquals(Matrix2by2.scale(a, 3), new Matrix2by2(3, 6, 9, 12)));
    check("transpose = [[1 3][2 4]]",
          Matrix2by2.approxEquals(Matrix2by2.transpose(a), new Matrix2by2(1, 3, 2, 4)));
  }

  private static void testDeterminant()
  {
    check("det [[1 2][3 4]] = -2",
          Matrix2by2.determinant(new Matrix2by2(1, 2, 3, 4)) == -2.0);
    check("det ROTATE_180 = 1", Matrix2by2.determinant(Matrix2by2.ROTATE_180) == 1.0);
  }

  private static void testMatrixApproxEquals()
  {
    check("approxEquals: equal matrices",
          Matrix2by2.approxEquals(new Matrix2by2(1, 2, 3, 4), new Matrix2by2(1, 2, 3, 4)));
    check("approxEquals: different matrices",
          !Matrix2by2.approxEquals(new Matrix2by2(1, 2, 3, 4), new Matrix2by2(1, 2, 3, 5)));
  }

  private static void testMatrixToString()
  {
    check("toString() is [[1.0 0.0] [0.0 1.0]]",
          Matrix2by2.IDENTITY.toString().equals("[[1.0 0.0] [0.0 1.0]]"));
  }

  private static void testVectorTimesMatrix()
  {
    // [1 2] * [[3 4][5 6]] = [1*3+2*5, 1*4+2*6] = [13 16]
    Vector1by2 result = Matrix2by2.multiply(v(1, 2), new Matrix2by2(3, 4, 5, 6));
    check("[1 2] * [[3 4][5 6]] = [13 16]", Vector1by2.approxEquals(result, v(13, 16)));
    check("v * IDENTITY = v",
          Vector1by2.approxEquals(Matrix2by2.multiply(v(7, -3), Matrix2by2.IDENTITY), v(7, -3)));
  }

  private static void testRotationMatrices()
  {
    check("[2 5] * ROTATE_180 = [-2 -5]",
          Vector1by2.approxEquals(Matrix2by2.multiply(v(2, 5), Matrix2by2.ROTATE_180), v(-2, -5)));
    check("[2 5] * ROTATE_90_CW = [5 -2]",
          Vector1by2.approxEquals(Matrix2by2.multiply(v(2, 5), Matrix2by2.ROTATE_90_CLOCKWISE), v(5, -2)));
    check("[2 5] * ROTATE_90_CCW = [-5 2]",
          Vector1by2.approxEquals(Matrix2by2.multiply(v(2, 5), Matrix2by2.ROTATE_90_COUNTERCLOCKWISE), v(-5, 2)));
  }
}
