package com.seedfinding.latticg.decomposition;

import com.seedfinding.latticg.math.component.Matrix;
import com.seedfinding.latticg.math.decomposition.LUDecomposition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;;


public class LUDecompositionTest {

    @Test
    public void testLU1() {
        Matrix m = Matrix.identityMatrix(4);
        LUDecomposition.Result result = LUDecomposition.decompose(m);
        assertTrue(m.equals(result.getP().transpose().multiply(result.getL().multiply(result.getU())), 0.0001F));
    }

    @Test
    public void testLU2() {
        Matrix m = Matrix.fromString("{{1, 5}, {2, -3}}");
        LUDecomposition.Result result = LUDecomposition.decompose(m);
        Matrix m2 = result.getP().transpose().multiply(result.getL().multiply(result.getU()));
        assertTrue(m.equals(m2, 0.0001F));
    }

    @Test
    public void testLU3() {
        Matrix m = Matrix.fromString("{{0, 5, 3}, {2, -3, 1}, {-9, 3, 4}}");
        LUDecomposition.Result result = LUDecomposition.decompose(m);
        assertTrue(m.equals(result.getP().transpose().multiply(result.getL().multiply(result.getU())), 0.0001F));
    }

    @Test
    public void testLU4() {
        Matrix m = Matrix.fromString("{{4, 3, 2, 1}, {1, 10, 3, 4}, {5, 3, 2, -4}, {4, 8, 7, 9}}");
        LUDecomposition.Result result = LUDecomposition.decompose(m);
        assertTrue(m.equals(result.getP().transpose().multiply(result.getL().multiply(result.getU())), 0.0001F));
    }

    @Test
    public void testDet1() {
        Matrix m = Matrix.identityMatrix(4);
        LUDecomposition.Result result = LUDecomposition.decompose(m);
        assertEquals(1.0D, result.getDet(), 0.0001D);
    }

    @Test
    public void testDet2() {
        Matrix m = Matrix.fromString("{{1, 5}, {2, -3}}");
        LUDecomposition.Result result = LUDecomposition.decompose(m);
        assertEquals(-13.0D, result.getDet(), 0.0001D);
    }

    @Test
    public void testDet3() {
        Matrix m = Matrix.fromString("{{0, 5, 3}, {2, -3, 1}, {-9, 3, 4}}");
        LUDecomposition.Result result = LUDecomposition.decompose(m);
        assertEquals(-148.0, result.getDet(), 0.0001D);
    }

    @Test
    public void testDet4() {
        Matrix m = Matrix.fromString("{{4, 3, 2, 1}, {1, 10, 3, 4}, {5, 3, 2, -4}, {4, 8, 7, 9}}");
        LUDecomposition.Result result = LUDecomposition.decompose(m);
        assertEquals(602.0D, result.getDet(), 0.0001D);
    }

    @Test
    public void testInverse1() {
        Matrix m = Matrix.identityMatrix(4);
        Matrix inverse = Matrix.identityMatrix(4);
        LUDecomposition.Result result = LUDecomposition.decompose(m);
        assertTrue(result.inverse().equals(inverse, 0.0001D));
    }

    @Test
    public void testInverse2() {
        Matrix m = Matrix.fromString("{{1, 5}, {2, -3}}");
        LUDecomposition.Result result = LUDecomposition.decompose(m);
        assertTrue(result.inverse().multiply(m).equals(Matrix.identityMatrix(m.getRowCount()), 0.0001D));
    }

    @Test
    public void testInverse3() {
        Matrix m = Matrix.fromString("{{0, 5, 3}, {2, -3, 1}, {-9, 3, 4}}");
        LUDecomposition.Result result = LUDecomposition.decompose(m);
        assertTrue(result.inverse().multiply(m).equals(Matrix.identityMatrix(m.getRowCount()), 0.0001D));
    }

    @Test
    public void testInverse4() {
        Matrix m = Matrix.fromString("{{4, 3, 2, 1}, {1, 10, 3, 4}, {5, 3, 2, -4}, {4, 8, 7, 9}}");
        LUDecomposition.Result result = LUDecomposition.decompose(m);
        assertTrue(result.inverse().multiply(m).equals(Matrix.identityMatrix(m.getRowCount()), 0.0001D));
    }

}
