package com.seedfinding.latticg.math.component;

import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** @noinspection CodeBlock2Expr */
public class MatrixTest {

    @Test
    public void testGetRowCount() {
        testMatrixFlavors(new Matrix(2, 3), m -> {
            assertEquals(2, m.getRowCount());
        });
    }

    @Test
    public void testGetColumnCount() {
        testMatrixFlavors(new Matrix(2, 3), m -> {
            assertEquals(3, m.getColumnCount());
        });
    }

    @Test
    public void testGet() {
        testMatrixFlavors(Matrix.fromString("{{2, 3}, {5, 7}}"), m -> {
            assertEquals(2, m.get(0, 0), 0);
            assertEquals(3, m.get(0, 1), 0);
            assertEquals(5, m.get(1, 0), 0);
            assertEquals(7, m.get(1, 1), 0);
        });
    }

    @Test
    public void testSet() {
        testMatrixFlavors(new Matrix(2, 2), m -> {
            m.set(0, 0, 2);
            m.set(0, 1, 3);
            m.set(1, 0, 5);
            m.set(1, 1, 7);
            assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m);
        });
    }

    @Test
    public void testGetRow() {
        testMatrixFlavors(Matrix.fromString("{{2, 3}, {5, 7}}"), m -> {
            assertEquals(new Vector(2, 3), m.getRow(0));
            assertEquals(new Vector(5, 7), m.getRow(1));
        });
    }

    @Test
    public void testGetColumn() {
        testMatrixFlavors(Matrix.fromString("{{2, 3}, {5, 7}}"), m -> {
            assertEquals(new Vector(2, 5), m.getColumn(0));
            assertEquals(new Vector(3, 7), m.getColumn(1));
        });
    }

    @Test
    public void testSetRow() {
        testMatrixFlavors(new Matrix(2, 2), m -> {
            m.setRow(0, new Vector(2, 3));
            m.setRow(1, new Vector(5, 7));
            assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m);
        });
    }

    @Test
    public void testSetColumn() {
        testMatrixFlavors(new Matrix(2, 2), m -> {
            m.setColumn(0, new Vector(2, 5));
            m.setColumn(1, new Vector(3, 7));
            assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m);
        });
    }

    @Test
    public void testAdd() {
        testMatrixFlavors2(Matrix.fromString("{{2, 3}, {5, 7}}"), Matrix.fromString("{{11, 13}, {17, 19}}"), (m1, m2) -> {
            assertEquals(Matrix.fromString("{{13, 16}, {22, 26}}"), m1.add(m2));
            assertEquals(Matrix.fromString("{{13, 16}, {22, 26}}"), m2.add(m1));
            assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m1);
        });
    }

    @Test
    public void testAddFail() {
        assertThrows(IllegalArgumentException.class, () -> new Matrix(2, 2).add(new Matrix(2, 3)));
    }

    @Test
    public void testSubtract() {
        testMatrixFlavors2(Matrix.fromString("{{2, 3}, {5, 7}}"), Matrix.fromString("{{11, 13}, {17, 19}}"), (m1, m2) -> {
            assertEquals(Matrix.fromString("{{9, 10}, {12, 12}}"), m2.subtract(m1));
            assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m1);
        });
    }

    @Test
    public void testSubtractFail() {
        assertThrows(IllegalArgumentException.class, () -> new Matrix(2, 2).subtract(new Matrix(2, 3)));
    }

    @Test
    public void testMultiplyScalar() {
        testMatrixFlavors(Matrix.fromString("{{2, 3}, {5, 7}}"), m -> {
            assertEquals(Matrix.fromString("{{4, 6}, {10, 14}}"), m.multiply(2));
            assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m);
        });
    }

    @Test
    public void testMultiplyMatrix1() {
        testMatrixFlavors2(Matrix.fromString("{{2, 3}, {5, 7}}"), Matrix.fromString("{{11, 13}, {17, 19}}"), (m1, m2) -> {
            assertEquals(Matrix.fromString("{{73, 83}, {174, 198}}"), m1.multiply(m2));
            assertEquals(Matrix.fromString("{{87, 124}, {129, 184}}"), m2.multiply(m1));
            assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m1);
        });
    }

    @Test
    public void testMultiplyMatrix2() {
        testMatrixFlavors(Matrix.fromString("{{2, 3}}"), m1 -> {
            testMatrixFlavors(Matrix.fromString("{{5}, {7}}"), m2 -> {
                assertEquals(Matrix.fromString("{{31}}"), m1.multiply(m2));
            });
        });
    }

    @Test
    public void testMultiplyMatrixFail() {
        assertThrows(IllegalArgumentException.class, () -> new Matrix(1, 2).multiply(new Matrix(1, 2)));
    }

    @Test
    public void testMultiplyVector() {
        testMatrixFlavors(Matrix.fromString("{{2, 3}, {5, 7}}"), m -> {
            Vector v = new Vector(11, 13);
            assertEquals(new Vector(61, 146), m.multiply(v));
            assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m);
        });
    }

    @Test
    public void testMultiplyVectorFail() {
        assertThrows(IllegalArgumentException.class, () -> new Matrix(2, 2).multiply(new Vector(3)));
    }

    @Test
    public void testDivide() {
        testMatrixFlavors(Matrix.fromString("{{4, 6}, {10, 14}}"), m -> {
            assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m.divide(2));
            assertEquals(Matrix.fromString("{{4, 6}, {10, 14}}"), m);
        });
    }

    @Test
    public void testInverseIdentity() {
        testMatrixFlavors(Matrix.identityMatrix(5), m -> {
            assertEquals(Matrix.identityMatrix(5), m.inverse());
        });
    }

    @Test
    public void testInverseOrthogonal() {
        testMatrixFlavors(Matrix.fromString("{{0, 1}, {-1, 0}}"), m -> {
            assertTrue(Matrix.fromString("{{0, -1}, {1, 0}}").equals(m.inverse(), 0.00000001));
            assertEquals(Matrix.fromString("{{0, 1}, {-1, 0}}"), m);
        });
    }

    @Test
    public void testInverseEight() {
        assertEquals(Matrix.fromString("{{0.125}}"), Matrix.fromString("{{8}}").inverse());
    }

    @Test
    public void testInverseSingular() {
        assertThrows(IllegalStateException.class, () -> {
            Matrix m = Matrix.fromString(
                "{{0, 1, 1, 2, 3}," +
                    "{5, 8, 13, 21, 34}," +
                    "{55, 89, 144, 233, 377}," +
                    "{610, 987, 1597, 2584, 4181}," +
                    "{6765, 10946, 17711, 28657, 46368}}"
            );
            m.inverse();
        });
    }

    @Test
    public void testInvesre5x5() {
        testMatrixFlavors(Matrix.fromString(
            "{{1, 8, 24, 7, 16}," +
                "{4, 5, 6, 22, 25}," +
                "{10, 20, 18, 12, 9}," +
                "{2, 13, 3, 23, 11}," +
                "{19, 14, 21, 15, 17}}"
        ), m -> {
            Matrix expected = Matrix.fromString(
                "{{-9516, -1107, -4077, -3855, 15237}," +
                    "{-12486, 16731, 39465, -18660, -21672}," +
                    "{16955, -18494, -20727, 15324, 12297}," +
                    "{9837, -20547, -31365, 31833, 16965}," +
                    "{-8706, 28434, 25335, -27342, -15984}}"
            ).divide(227079);
            assertTrue(m.inverse().equals(expected, 0.00000001));
        });
    }

    @Test
    public void testInverseNonSquare() {
        assertThrows(UnsupportedOperationException.class, () -> new Matrix(1, 2).inverse());
    }

    @Test
    public void testSwapRows() {
        testMatrixFlavors(Matrix.fromString("{{1, 2}, {3, 4}, {5, 7}}"), m -> {
            assertEquals(Matrix.fromString("{{5, 7}, {3, 4}, {1, 2}}"), m.swapRows(0, 2));
            assertEquals(Matrix.fromString("{{1, 2}, {3, 4}, {5, 7}}"), m);
        });
    }

    @Test
    public void testTranspose() {
        testMatrixFlavors(Matrix.fromString("{{1, 2}, {3, 4}, {5, 7}}"), m -> {
            assertEquals(Matrix.fromString("{{1, 3, 5}, {2, 4, 7}}"), m.transpose());
            assertEquals(Matrix.fromString("{{1, 2}, {3, 4}, {5, 7}}"), m);
        });
    }

    @Test
    public void testAddAndSet() {
        testMatrixFlavors2(Matrix.fromString("{{2, 3}, {5, 7}}"), Matrix.fromString("{{11, 13}, {17, 19}}"), (m1, m2) -> {
            Matrix result = m1.addAndSet(m2);
            assertSame(result, m1);
            assertEquals(Matrix.fromString("{{13, 16}, {22, 26}}"), m1);
        });
    }

    @Test
    public void testAddAndSetFail() {
        assertThrows(IllegalArgumentException.class, () -> new Matrix(2, 2).addAndSet(new Matrix(2, 3)));
    }

    @Test
    public void testSubtractAndSet() {
        testMatrixFlavors2(Matrix.fromString("{{2, 3}, {5, 7}}"), Matrix.fromString("{{11, 13}, {17, 19}}"), (m1, m2) -> {
            Matrix result = m1.subtractAndSet(m2);
            assertSame(result, m1);
            assertEquals(Matrix.fromString("{{-9, -10}, {-12, -12}}"), m1);
        });
    }

    @Test
    public void testSubtractAndSetFail() {
        assertThrows(IllegalArgumentException.class, () -> new Matrix(2, 2).subtractAndSet(new Matrix(2, 3)));
    }

    @Test
    public void testMultiplyAndSetScalar() {
        testMatrixFlavors(Matrix.fromString("{{2, 3}, {5, 7}}"), m -> {
            Matrix result = m.multiplyAndSet(2);
            assertSame(result, m);
            assertEquals(Matrix.fromString("{{4, 6}, {10, 14}}"), m);
        });
    }

    @Test
    public void testMultiplyAndSetMatrix() {
        testMatrixFlavors2(Matrix.fromString("{{2, 3}, {5, 7}}"), Matrix.fromString("{{11, 13}, {17, 19}}"), (m1, m2) -> {
            Matrix result = m1.multiplyAndSet(m2);
            assertSame(result, m1);
            assertEquals(Matrix.fromString("{{73, 83}, {174, 198}}"), m1);
        });
    }

    @Test
    public void testMultiplyAndSetMatrixFail() {
        assertThrows(IllegalArgumentException.class, () -> new Matrix(1, 2).multiplyAndSet(new Matrix(1, 2)));
    }

    @Test
    public void testMultiplyAndSetNonSquare() {
        assertThrows(IllegalArgumentException.class, () -> new Matrix(2, 2).multiplyAndSet(new Matrix(2, 3)));
    }

    @Test
    public void testDivideAndSetScalar() {
        testMatrixFlavors(Matrix.fromString("{{4, 6}, {10, 14}}"), m -> {
            Matrix result = m.divideAndSet(2);
            assertSame(result, m);
            assertEquals(Matrix.fromString("{{2, 3}, {5, 7}}"), m);
        });
    }

    @Test
    public void testSwapRowsAndSet() {
        testMatrixFlavors(Matrix.fromString("{{1, 2}, {3, 4}, {5, 7}}"), m -> {
            Matrix result = m.swapRowsAndSet(0, 2);
            assertSame(result, m);
            assertEquals(Matrix.fromString("{{5, 7}, {3, 4}, {1, 2}}"), m);
        });
    }

    @Test
    public void copy() {
        testMatrixFlavors(Matrix.fromString("{{1, 2}, {3, 4}}"), m -> {
            Matrix result = m.copy();
            assertNotSame(m, result);
            assertEquals(Matrix.fromString("{{1, 2}, {3, 4}}"), result);
        });
    }

    private void testMatrixFlavors(Matrix m, Consumer<Matrix> tester) {
        tester.accept(m.copy());
        tester.accept(createSubmatrix(m));
    }

    private void testMatrixFlavors2(Matrix m1, Matrix m2, BiConsumer<Matrix, Matrix> tester) {
        tester.accept(m1.copy(), m2.copy());
        tester.accept(m1.copy(), createSubmatrix(m2));
        tester.accept(createSubmatrix(m1), m2.copy());
        tester.accept(createSubmatrix(m1), createSubmatrix(m2));
    }

    private Matrix createSubmatrix(Matrix m) {
        Matrix supermatrix = new Matrix(m.getRowCount() + 2, m.getColumnCount() + 2, (row, col) -> {
            if (row == 0 || col == 0 || row == m.getRowCount() + 1 || col == m.getColumnCount() + 1) {
                return 47 + row + 31 * col;
            } else {
                return m.get(row - 1, col - 1);
            }
        });
        return supermatrix.submatrix(1, 1, m.getRowCount(), m.getColumnCount());
    }
}
