package randomreverser.math.component;

import org.junit.Test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/** @noinspection CodeBlock2Expr */
public class BigMatrixTest {

    @Test
    public void testGetRowCount() {
        assertEquals(2, new BigMatrix(2, 3).getRowCount());
    }

    @Test
    public void testGetColumnCount() {
        assertEquals(3, new BigMatrix(2, 3).getColumnCount());
    }

    @Test
    public void testGet() {
        testMatrixFlavors(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m -> {
            assertEquals(new BigFraction(2), m.get(0, 0));
            assertEquals(new BigFraction(3), m.get(0, 1));
            assertEquals(new BigFraction(5), m.get(1, 0));
            assertEquals(new BigFraction(7), m.get(1, 1));
        });
    }

    @Test
    public void testSet() {
        testMatrixFlavors(new BigMatrix(2, 2), m -> {
            m.set(0, 0, new BigFraction(2));
            m.set(0, 1, new BigFraction(3));
            m.set(1, 0, new BigFraction(5));
            m.set(1, 1, new BigFraction(7));
            assertEquals(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m);
        });
    }

    @Test
    public void testGetRow() {
        testMatrixFlavors(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m -> {
            assertEquals(new BigVector(2, 3), m.getRow(0));
            assertEquals(new BigVector(5, 7), m.getRow(1));
        });
    }

    @Test
    public void testGetColumn() {
        testMatrixFlavors(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m -> {
            assertEquals(new BigVector(2, 5), m.getColumn(0));
            assertEquals(new BigVector(3, 7), m.getColumn(1));
        });
    }

    @Test
    public void testSetRow() {
        testMatrixFlavors(new BigMatrix(2, 2), m -> {
            m.setRow(0, new BigVector(2, 3));
            m.setRow(1, new BigVector(5, 7));
            assertEquals(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m);
        });
    }

    @Test
    public void testSetColumn() {
        testMatrixFlavors(new BigMatrix(2, 2), m -> {
            m.setColumn(0, new BigVector(2, 5));
            m.setColumn(1, new BigVector(3, 7));
            assertEquals(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m);
        });
    }

    @Test
    public void testAdd() {
        testMatrixFlavors2(BigMatrix.fromString("{{2, 3}, {5, 7}}"), BigMatrix.fromString("{{11, 13}, {17, 19}}"), (m1, m2) -> {
            assertEquals(BigMatrix.fromString("{{13, 16}, {22, 26}}"), m1.add(m2));
            assertEquals(BigMatrix.fromString("{{13, 16}, {22, 26}}"), m2.add(m1));
            assertEquals(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m1);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFail() {
        new BigMatrix(2, 2).add(new BigMatrix(2, 3));
    }

    @Test
    public void testSubtract() {
        testMatrixFlavors2(BigMatrix.fromString("{{2, 3}, {5, 7}}"), BigMatrix.fromString("{{11, 13}, {17, 19}}"), (m1, m2) -> {
            assertEquals(BigMatrix.fromString("{{9, 10}, {12, 12}}"), m2.subtract(m1));
            assertEquals(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m1);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubtractFail() {
        new BigMatrix(2, 2).subtract(new BigMatrix(2, 3));
    }

    @Test
    public void testMultiplyScalar() {
        testMatrixFlavors(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m -> {
            assertEquals(BigMatrix.fromString("{{4, 6}, {10, 14}}"), m.multiply(new BigFraction(2)));
            assertEquals(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m);
        });
    }

    @Test
    public void testMultiplyMatrix1() {
        testMatrixFlavors2(BigMatrix.fromString("{{2, 3}, {5, 7}}"), BigMatrix.fromString("{{11, 13}, {17, 19}}"), (m1, m2) -> {
            assertEquals(BigMatrix.fromString("{{73, 83}, {174, 198}}"), m1.multiply(m2));
            assertEquals(BigMatrix.fromString("{{87, 124}, {129, 184}}"), m2.multiply(m1));
            assertEquals(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m1);
        });
    }

    @Test
    public void testMultiplyMatrix2() {
        testMatrixFlavors2(BigMatrix.fromString("{{2, 3}}"), BigMatrix.fromString("{{5}, {7}}"), (m1, m2) -> {
            assertEquals(BigMatrix.fromString("{{31}}"), m1.multiply(m2));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiplyMatrixFail() {
        new BigMatrix(1, 2).multiply(new BigMatrix(1, 2));
    }

    @Test
    public void testMultiplyVector() {
        testMatrixFlavors(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m -> {
            BigVector v = new BigVector(11, 13);
            assertEquals(new BigVector(61, 146), m.multiply(v));
            assertEquals(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiplyVectorFail() {
        new BigMatrix(2, 2).multiply(new BigVector(3));
    }

    @Test
    public void testDivide() {
        testMatrixFlavors(BigMatrix.fromString("{{4, 6}, {10, 14}}"), m -> {
            assertEquals(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m.divide(new BigFraction(2)));
            assertEquals(BigMatrix.fromString("{{4, 6}, {10, 14}}"), m);
        });
    }

    @Test
    public void testInverseIdentity() {
        assertEquals(BigMatrix.identityMatrix(5), BigMatrix.identityMatrix(5).inverse());
    }

    @Test
    public void testInverseOrthogonal() {
        testMatrixFlavors(BigMatrix.fromString("{{0, 1}, {-1, 0}}"), m -> {
            assertEquals(BigMatrix.fromString("{{0, -1}, {1, 0}}"), m.inverse());
            assertEquals(BigMatrix.fromString("{{0, 1}, {-1, 0}}"), m);
        });
    }

    @Test
    public void testInverseEight() {
        assertEquals(BigMatrix.fromString("{{1/8}}"), BigMatrix.fromString("{{8}}").inverse());
    }

    @Test(expected = IllegalStateException.class)
    public void testInverseSingular() {
        BigMatrix m = BigMatrix.fromString(
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
        testMatrixFlavors(BigMatrix.fromString(
                "{{1, 8, 24, 7, 16}," +
                        "{4, 5, 6, 22, 25}," +
                        "{10, 20, 18, 12, 9}," +
                        "{2, 13, 3, 23, 11}," +
                        "{19, 14, 21, 15, 17}}"
        ), m -> {
            BigMatrix expected = BigMatrix.fromString(
                    "{{-9516, -1107, -4077, -3855, 15237}," +
                            "{-12486, 16731, 39465, -18660, -21672}," +
                            "{16955, -18494, -20727, 15324, 12297}," +
                            "{9837, -20547, -31365, 31833, 16965}," +
                            "{-8706, 28434, 25335, -27342, -15984}}"
            ).divide(new BigFraction(227079));
            assertEquals(expected, m.inverse());
        });
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInverseNonSquare() {
        new BigMatrix(1, 2).inverse();
    }

    @Test
    public void testSwapRows() {
        testMatrixFlavors(BigMatrix.fromString("{{1, 2}, {3, 4}, {5, 7}}"), m -> {
            assertEquals(BigMatrix.fromString("{{5, 7}, {3, 4}, {1, 2}}"), m.swapRows(0, 2));
            assertEquals(BigMatrix.fromString("{{1, 2}, {3, 4}, {5, 7}}"), m);
        });
    }

    @Test
    public void testTranspose() {
        testMatrixFlavors(BigMatrix.fromString("{{1, 2}, {3, 4}, {5, 7}}"), m -> {
            assertEquals(BigMatrix.fromString("{{1, 3, 5}, {2, 4, 7}}"), m.transpose());
            assertEquals(BigMatrix.fromString("{{1, 2}, {3, 4}, {5, 7}}"), m);
        });
    }

    @Test
    public void testAddEquals() {
        testMatrixFlavors2(BigMatrix.fromString("{{2, 3}, {5, 7}}"), BigMatrix.fromString("{{11, 13}, {17, 19}}"), (m1, m2) -> {
            BigMatrix result = m1.addEquals(m2);
            assertSame(result, m1);
            assertEquals(BigMatrix.fromString("{{13, 16}, {22, 26}}"), m1);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEqualsFail() {
        new BigMatrix(2, 2).addEquals(new BigMatrix(2, 3));
    }

    @Test
    public void testSubtractEquals() {
        testMatrixFlavors2(BigMatrix.fromString("{{2, 3}, {5, 7}}"), BigMatrix.fromString("{{11, 13}, {17, 19}}"), (m1, m2) -> {
            BigMatrix result = m1.subtractEquals(m2);
            assertSame(result, m1);
            assertEquals(BigMatrix.fromString("{{-9, -10}, {-12, -12}}"), m1);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubtractEqualsFail() {
        new BigMatrix(2, 2).subtractEquals(new BigMatrix(2, 3));
    }

    @Test
    public void testMultiplyEquals() {
        testMatrixFlavors2(BigMatrix.fromString("{{2, 3}, {5, 7}}"), BigMatrix.fromString("{{11, 13}, {17, 19}}"), (m1, m2) -> {
            BigMatrix result = m1.multiplyEquals(m2);
            assertSame(result, m1);
            assertEquals(BigMatrix.fromString("{{73, 83}, {174, 198}}"), m1);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiplyEqualsFail() {
        new BigMatrix(1, 2).multiplyEquals(new BigMatrix(1, 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiplyEqualsNonSquare() {
        new BigMatrix(2, 2).multiplyEquals(new BigMatrix(2, 3));
    }

    @Test
    public void testDivideEqualsScalar() {
        testMatrixFlavors(BigMatrix.fromString("{{4, 6}, {10, 14}}"), m -> {
            BigMatrix result = m.divideEquals(new BigFraction(2));
            assertSame(result, m);
            assertEquals(BigMatrix.fromString("{{2, 3}, {5, 7}}"), m);
        });
    }

    @Test
    public void testSwapRowsEquals() {
        testMatrixFlavors(BigMatrix.fromString("{{1, 2}, {3, 4}, {5, 7}}"), m -> {
            BigMatrix result = m.swapRowsEquals(0, 2);
            assertSame(result, m);
            assertEquals(BigMatrix.fromString("{{5, 7}, {3, 4}, {1, 2}}"), m);
        });
    }

    @Test
    public void testSwapElementsEquals() {
        testMatrixFlavors(BigMatrix.fromString("{{1, 2}, {3, 4}, {5, 7}}"), m -> {
            BigMatrix result = m.swapElementsEquals(0,1, 2,0);
            assertSame(result, m);
            assertEquals(BigMatrix.fromString("{{1, 5}, {3, 4}, {2, 7}}"), m);
        });
    }


    @Test
    public void copy() {
        testMatrixFlavors(BigMatrix.fromString("{{1, 2}, {3, 4}}"), m -> {
            BigMatrix result = m.copy();
            assertNotSame(m, result);
            assertEquals(BigMatrix.fromString("{{1, 2}, {3, 4}}"), result);
        });
    }

    @Test
    public void testShiftRows(){
        testMatrixFlavors(BigMatrix.fromString("{{1, 2}, {3, 4}}"),m -> {
            BigMatrix result=m.shiftRows(0,1);
            assertSame(result, m);
            assertEquals(BigMatrix.fromString("{{3, 4}, {1, 2}}"),m);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShiftRowsFail(){
        testMatrixFlavors(BigMatrix.fromString("{{1, 2}, {3, 4}}"),m -> {
            BigMatrix result=m.shiftRows(1,0);
        });
    }

    @Test()
    public void testShiftRowsNothing(){
        testMatrixFlavors(BigMatrix.fromString("{{1, 2}, {3, 4}}"),m -> {
            BigMatrix result=m.shiftRows(1,1);
            assertSame(result, m);
            assertEquals(BigMatrix.fromString("{{1, 2}, {3, 4}}"),m);
        });
    }

    private void testMatrixFlavors(BigMatrix m, Consumer<BigMatrix> tester) {
        tester.accept(m.copy());
        tester.accept(createSubmatrix(m));
    }

    private void testMatrixFlavors2(BigMatrix m1, BigMatrix m2, BiConsumer<BigMatrix, BigMatrix> tester) {
        tester.accept(m1.copy(), m2.copy());
        tester.accept(m1.copy(), createSubmatrix(m2));
        tester.accept(createSubmatrix(m1), m2.copy());
        tester.accept(createSubmatrix(m1), createSubmatrix(m2));
    }

    private BigMatrix createSubmatrix(BigMatrix m) {
        BigMatrix supermatrix = new BigMatrix(m.getRowCount() + 2, m.getColumnCount() + 2, (row, col) -> {
            if (row == 0 || col == 0 || row == m.getRowCount() + 1 || col == m.getColumnCount() + 1) {
                return new BigFraction(47 + row + 31 * col);
            } else {
                return m.get(row - 1, col - 1);
            }
        });
        return supermatrix.submatrix(1, 1, m.getRowCount(), m.getColumnCount());
    }
}
