package randomreverser.math.component;

import org.junit.Test;

import static org.junit.Assert.*;

public class MatrixTest {

    @Test
    public void testGetRowCount() {
        assertEquals(2, new Matrix(2, 3).getRowCount());
    }

    @Test
    public void testGetColumnCount() {
        assertEquals(3, new Matrix(2, 3).getColumnCount());
    }

    @Test
    public void testGet() {
        Matrix m = Matrix.fromString("{{2, 3}, {5, 7}}");
        assertEquals(2, m.get(0, 0), 0);
        assertEquals(3, m.get(0, 1), 0);
        assertEquals(5, m.get(1, 0), 0);
        assertEquals(7, m.get(1, 1), 0);
    }

    @Test
    public void testSet() {
        Matrix m = new Matrix(2, 2);
        m.set(0, 0, 2);
        m.set(0, 1, 3);
        m.set(1, 0, 5);
        m.set(1, 1, 7);
        assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m);
    }

    @Test
    public void testGetRow() {
        Matrix m = Matrix.fromString("{{2, 3}, {5, 7}}");
        assertEquals(new Vector(2, 3), m.getRow(0));
        assertEquals(new Vector(5, 7), m.getRow(1));
    }

    @Test
    public void testGetColumn() {
        Matrix m = Matrix.fromString("{{2, 3}, {5, 7}}");
        assertEquals(new Vector(2, 5), m.getColumn(0));
        assertEquals(new Vector(3, 7), m.getColumn(1));
    }

    @Test
    public void testSetRow() {
        Matrix m = new Matrix(2, 2);
        m.setRow(0, new Vector(2, 3));
        m.setRow(1, new Vector(5, 7));
        assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m);
    }

    @Test
    public void testSetColumn() {
        Matrix m = new Matrix(2, 2);
        m.setColumn(0, new Vector(2, 5));
        m.setColumn(1, new Vector(3, 7));
        assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m);
    }

    @Test
    public void testAdd() {
        Matrix m1 = Matrix.fromString("{{2, 3}, {5, 7}}");
        Matrix m2 = Matrix.fromString("{{11, 13}, {17, 19}}");
        assertEquals(Matrix.fromString("{{13, 16}, {22, 26}}"), m1.add(m2));
        assertEquals(Matrix.fromString("{{13, 16}, {22, 26}}"), m2.add(m1));
        assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFail() {
        new Matrix(2, 2).add(new Matrix(2, 3));
    }

    @Test
    public void testSubtract() {
        Matrix m1 = Matrix.fromString("{{2, 3}, {5, 7}}");
        Matrix m2 = Matrix.fromString("{{11, 13}, {17, 19}}");
        assertEquals(Matrix.fromString("{{9, 10}, {12, 12}}"), m2.subtract(m1));
        assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubtractFail() {
        new Matrix(2, 2).subtract(new Matrix(2, 3));
    }

    @Test
    public void testMultiplyScalar() {
        Matrix m = Matrix.fromString("{{2, 3}, {5, 7}}");
        assertEquals(Matrix.fromString("{{4, 6}, {10, 14}}"), m.multiply(2));
        assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m);
    }

    @Test
    public void testMultiplyMatrix1() {
        Matrix m1 = Matrix.fromString("{{2, 3}, {5, 7}}");
        Matrix m2 = Matrix.fromString("{{11, 13}, {17, 19}}");
        assertEquals(Matrix.fromString("{{73, 83}, {174, 198}}"), m1.multiply(m2));
        assertEquals(Matrix.fromString("{{87, 124}, {129, 184}}"), m2.multiply(m1));
        assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m1);
    }

    @Test
    public void testMultiplyMatrix2() {
        Matrix m1 = Matrix.fromString("{{2, 3}}");
        Matrix m2 = Matrix.fromString("{{5}, {7}}");
        assertEquals(Matrix.fromString("{{31}}"), m1.multiply(m2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiplyMatrixFail() {
        new Matrix(1, 2).multiply(new Matrix(1, 2));
    }

    @Test
    public void testMultiplyVector() {
        Matrix m = Matrix.fromString("{{2, 3}, {5, 7}}");
        Vector v = new Vector(11, 13);
        assertEquals(new Vector(61, 146), m.multiply(v));
        assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiplyVectorFail() {
        new Matrix(2, 2).multiply(new Vector(3));
    }

    @Test
    public void testDivide() {
        Matrix m = Matrix.fromString("{{4, 6}, {10, 14}}");
        assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m.divide(2));
        assertEquals(Matrix.fromString("{{4, 6}, {10, 14}}"), m);
    }

    @Test
    public void testInverseIdentity() {
        assertEquals(Matrix.identityMatrix(5), Matrix.identityMatrix(5).inverse());
    }

    @Test
    public void testInverseOrthogonal() {
        Matrix m = Matrix.fromString("{{0, 1}, {-1, 0}}");
        assertEquals(Matrix.fromString("{{0, -1}, {1, 0}}"), m.inverse());
        assertEquals(Matrix.fromString("{{0, 1}, {-1, 0}}"), m);
    }

    @Test
    public void testInverseEight() {
        assertEquals(Matrix.fromString("{{0.125}}"), Matrix.fromString("{{8}}").inverse());
    }

    @Test(expected = IllegalStateException.class)
    public void testInverseSingular() {
        Matrix m = Matrix.fromString(
                "{{0, 1, 1, 2, 3}," +
                "{5, 8, 13, 21, 34}," +
                "{55, 89, 144, 233, 377}," +
                "{610, 987, 1597, 2584, 4181}," +
                "{6765, 10946, 17711, 28657, 46368}}"
        );
        m.inverse();
    }

    @Test
    public void testInvesre5x5() {
        Matrix m = Matrix.fromString(
                "{{1, 8, 24, 7, 16}," +
                "{4, 5, 6, 22, 25}," +
                "{10, 20, 18, 12, 9}," +
                "{2, 13, 3, 23, 11}," +
                "{19, 14, 21, 15, 17}}"
        );
        Matrix expected = Matrix.fromString(
                "{{-9516, -1107, -4077, -3855, 15237}," +
                "{-12486, 16731, 39465, -18660, -21672}," +
                "{16955, -18494, -20727, 15324, 12297}," +
                "{9837, -20547, -31365, 31833, 16965}," +
                "{-8706, 28434, 25335, -27342, -15984}}"
        ).divide(227079);
        assertTrue(m.inverse().equals(expected, 0.00000001));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInverseNonSquare() {
        new Matrix(1, 2).inverse();
    }

    @Test
    public void testSwapRows() {
        Matrix m = Matrix.fromString("{{1, 2}, {3, 4}, {5, 7}}");
        assertEquals(Matrix.fromString("{{5, 7}, {3, 4}, {1, 2}}"), m.swapRows(0, 2));
        assertEquals(Matrix.fromString("{{1, 2}, {3, 4}, {5, 7}}"), m);
    }

    @Test
    public void testTranspose() {
        Matrix m = Matrix.fromString("{{1, 2}, {3, 4}, {5, 7}}");
        assertEquals(Matrix.fromString("{{1, 3, 5}, {2, 4, 7}}"), m.transpose());
        assertEquals(Matrix.fromString("{{1, 2}, {3, 4}, {5, 7}}"), m);
    }

    @Test
    public void testAddEquals() {
        Matrix m1 = Matrix.fromString("{{2, 3}, {5, 7}}");
        Matrix m2 = Matrix.fromString("{{11, 13}, {17, 19}}");
        Matrix result = m1.addEquals(m2);
        assertSame(result, m1);
        assertEquals(Matrix.fromString("{{13, 16}, {22, 26}}"), m1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEqualsFail() {
        new Matrix(2, 2).addEquals(new Matrix(2, 3));
    }

    @Test
    public void testSubtractEquals() {
        Matrix m1 = Matrix.fromString("{{2, 3}, {5, 7}}");
        Matrix m2 = Matrix.fromString("{{11, 13}, {17, 19}}");
        Matrix result = m1.subtractEquals(m2);
        assertSame(result, m1);
        assertEquals(Matrix.fromString("{{-9, -10}, {-12, -12}}"), m1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubtractEqualsFail() {
        new Matrix(2, 2).subtractEquals(new Matrix(2, 3));
    }

    @Test
    public void testMultiplyEqualsScalar() {
        Matrix m = Matrix.fromString("{{2, 3}, {5, 7}}");
        Matrix result = m.multiplyEquals(2);
        assertSame(result, m);
        assertEquals(Matrix.fromString("{{4, 6}, {10, 14}}"), m);
    }

    @Test
    public void testMultiplyEqualsMatrix() {
        Matrix m1 = Matrix.fromString("{{2, 3}, {5, 7}}");
        Matrix m2 = Matrix.fromString("{{11, 13}, {17, 19}}");
        Matrix result = m1.multiplyEquals(m2);
        assertSame(result, m1);
        assertEquals(Matrix.fromString("{{73, 83}, {174, 198}}"), m1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiplyEqualsMatrixFail() {
        new Matrix(1, 2).multiplyEquals(new Matrix(1, 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiplyEqualsNonSquare() {
        new Matrix(2, 2).multiplyEquals(new Matrix(2, 3));
    }

    @Test
    public void testDivideEqualsScalar() {
        Matrix m = Matrix.fromString("{{4, 6}, {10, 14}}");
        Matrix result = m.divideEquals(2);
        assertSame(result, m);
        assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m);
    }

    @Test
    public void testSwapRowsEquals() {
        Matrix m = Matrix.fromString("{{1, 2}, {3, 4}, {5, 7}}");
        Matrix result = m.swapRowsEquals(0, 2);
        assertSame(result, m);
        assertEquals(Matrix.fromString("{{5, 7}, {3, 4}, {1, 2}}"), m);
    }

    @Test
    public void copy() {
        Matrix m = Matrix.fromString("{{1, 2}, {3, 4}}");
        Matrix result = m.copy();
        assertNotSame(m, result);
        assertEquals(Matrix.fromString("{{1, 2}, {3, 4}}"), result);
    }
}
