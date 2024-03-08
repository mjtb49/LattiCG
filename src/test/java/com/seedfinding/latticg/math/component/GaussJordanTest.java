package com.seedfinding.latticg.math.component;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GaussJordanTest {
    @Test
    public void test1() {
        BigMatrix m = BigMatrixUtil.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 10}}");
        BigMatrix copy = m.copy();
        BigMatrix inverse = BigMatrix.identityMatrix(m.getRowCount());

        GaussJordan.reduce(m, inverse);

        assertEquals(copy.multiply(inverse), BigMatrix.identityMatrix(m.getRowCount()));
    }

    @Test
    public void test2() {
        BigMatrix m = BigMatrixUtil.fromString("{{8, 2, 3, 3, 1}, {5, 8, 7, 9, 2}, {3, 3, 2, 4, 3}, {1, 1, 9, 3, 3}, {2, 5, 7, 2, 3}}");
        BigMatrix copy = m.copy();
        BigMatrix inverse = BigMatrix.identityMatrix(m.getRowCount());

        GaussJordan.reduce(m, inverse);

        assertEquals(copy.multiply(inverse), BigMatrix.identityMatrix(m.getRowCount()));
    }

    @Test
    public void test3() {
        BigMatrix m = BigMatrixUtil.fromString("{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}, {11, 11, 11, 11, 12}, {13, 14, 15, 16, 18}}");

        int[] pivotRows = GaussJordan.reduce(m);

        assertArrayEquals(pivotRows, new int[] {0, 1, -1, -1, 2});
        assertEquals(m, BigMatrixUtil.fromString("{{1, 0, -1, -2, 0}, {0, 1, 2, 3, 0}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 0}}"));
    }

    @Test
    public void test4() {
        BigMatrix m = BigMatrixUtil.fromString("{{1, 0, 0, 0},  {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}}");
        int[] pivotRows = GaussJordan.reduce(m);

        assertArrayEquals(pivotRows, new int[] {0, 1, -1, -1});
        assertEquals(m, BigMatrixUtil.fromString("{{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}}"));
    }
}
