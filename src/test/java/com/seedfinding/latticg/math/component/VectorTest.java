package com.seedfinding.latticg.math.component;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VectorTest {

    @Test
    public void testGetDimension0() {
        assertEquals(0, new Vector(0).getDimension());
    }

    @Test
    public void testGetDimension3() {
        assertEquals(3, new Vector(3).getDimension());
    }

    @Test
    public void testGet() {
        assertEquals(4, new Vector(1, 2, 3, 4, 5).get(3), 0);
    }

    @Test
    public void testMagnitude() {
        assertEquals(5, new Vector(3, 4).magnitude(), 0.00000001);
    }

    @Test
    public void testMagnitudeSq() {
        assertEquals(55, new Vector(1, 2, 3, 4, 5).magnitudeSq(), 0);
    }

    @Test
    public void testIsZeroTrue() {
        assertTrue(new Vector(0, 0, 0, 0, 0).isZero());
    }

    @Test
    public void testIsZeroFalse1() {
        assertFalse(new Vector(1, 2, 3, 4, 5).isZero());
    }

    @Test
    public void testIsZeroFalse2() {
        assertFalse(new Vector(1, -1).isZero());
    }

    @Test
    public void testAdd() {
        Vector a = new Vector(1, 2, 3, 4, 5);
        Vector b = new Vector(5, 3, 6, -4, -1);
        assertEquals(new Vector(6, 5, 9, 0, 4), a.add(b));
        assertEquals(new Vector(1, 2, 3, 4, 5), a);
    }

    @Test
    public void testAddFail() {
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2).add(new Vector(3, 4, 5)));
    }

    @Test
    public void testSubtract() {
        Vector a = new Vector(1, 2, 3, 4, 5);
        Vector b = new Vector(-5, -3, -6, 4, 1);
        assertEquals(new Vector(6, 5, 9, 0, 4), a.subtract(b));
        assertEquals(new Vector(1, 2, 3, 4, 5), a);
    }

    @Test
    public void testSubtractFail() {
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2).subtract(new Vector(3, 4, 5)));
    }

    @Test
    public void testMultiplyScalar() {
        Vector a = new Vector(1, 2, 3, 4, 5);
        assertEquals(new Vector(2, 4, 6, 8, 10), a.multiply(2));
        assertEquals(new Vector(1, 2, 3, 4, 5), a);
    }

    @Test
    public void testMultiplyMatrix1() {
        Vector a = new Vector(2, 3);
        Matrix b = Matrix.fromString("{{5, 7}, {11, 13}}");
        assertEquals(new Vector(43, 53), a.multiply(b));
        assertEquals(new Vector(2, 3), a);
    }

    @Test
    public void testMultiplyMatrix2() {
        Vector a = new Vector(2, 3, 5);
        Matrix b = Matrix.fromString("{{7, 11}, {13, 17}, {19, 23}}");
        assertEquals(new Vector(148, 188), a.multiply(b));
        assertEquals(new Vector(2, 3, 5), a);
    }

    @Test
    public void testMultiplyMatrixFail() {
        assertThrows(IllegalArgumentException.class, () -> {
            Vector a = new Vector(2, 3, 5);
            Matrix b = Matrix.fromString("{{7, 11, 13}, {17, 19, 23}}");
            a.multiply(b);
        });
    }

    @Test
    public void testDivide() {
        Vector a = new Vector(2, 4);
        assertEquals(new Vector(1, 2), a.divide(2));
        assertEquals(new Vector(2, 4), a);
    }

    @Test
    public void testSwapNums() {
        Vector a = new Vector(1, 2, 3);
        assertEquals(new Vector(1, 3, 2), a.swapNums(1, 2));
        assertEquals(new Vector(1, 2, 3), a);
    }

    @Test
    public void testAddAndSet() {
        Vector a = new Vector(1, 2, 3, 4, 5);
        Vector b = new Vector(5, 3, 6, -4, -1);
        Vector result = a.addAndSet(b);
        assertSame(result, a);
        assertEquals(new Vector(6, 5, 9, 0, 4), a);
    }

    @Test
    public void testAddAndSetFail() {
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2).addAndSet(new Vector(3, 4, 5)));
    }

    @Test
    public void testSubtractAndSet() {
        Vector a = new Vector(1, 2, 3, 4, 5);
        Vector b = new Vector(-5, -3, -6, 4, 1);
        Vector result = a.subtractAndSet(b);
        assertSame(result, a);
        assertEquals(new Vector(6, 5, 9, 0, 4), a);
    }

    @Test
    public void testSubtractAndSetFail() {
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2).subtractAndSet(new Vector(3, 4, 5)));
    }

    @Test
    public void testMultiplyAndSet() {
        Vector a = new Vector(1, 2, 3, 4, 5);
        Vector result = a.multiplyAndSet(2);
        assertSame(result, a);
        assertEquals(new Vector(2, 4, 6, 8, 10), a);
    }

    @Test
    public void divideAndSet() {
        Vector a = new Vector(2, 4);
        Vector result = a.divideAndSet(2);
        assertSame(result, a);
        assertEquals(new Vector(1, 2), a);
    }

    @Test
    public void testSwapNumsAndSet() {
        Vector a = new Vector(1, 2, 3);
        Vector result = a.swapNumsAndSet(1, 2);
        assertSame(result, a);
        assertEquals(new Vector(1, 3, 2), a);
    }

    @Test
    public void testDot() {
        Vector a = new Vector(2, 3, 5);
        Vector b = new Vector(7, 11, 13);
        assertEquals(112, a.dot(b), 0);
        assertEquals(112, b.dot(a), 0);
    }

    @Test
    public void testDotFail() {
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2).dot(new Vector(3, 4, 5)));
    }

    @Test
    public void testGramSchmidtCoefficient() {
        Vector a = new Vector(7, 11, 13);
        Vector b = new Vector(2, 3, 5);
        assertEquals(56.0 / 19, a.gramSchmidtCoefficient(b), 0.00000001);
    }

    @Test
    public void testGramSchmidtCoefficientFail() {
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2).gramSchmidtCoefficient(new Vector(3, 4, 5)));
    }

    @Test
    public void testProjectOnto() {
        Vector a = new Vector(7, 11, 13);
        Vector b = new Vector(2, 3, 5);
        assertTrue(new Vector(112.0 / 19, 168.0 / 19, 280.0 / 19).equals(a.projectOnto(b), 0.00000001));
    }

    @Test
    public void testProjectOntoFail() {
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2).projectOnto(new Vector(3, 4, 5)));
    }

    @Test
    public void testCopy() {
        Vector a = new Vector(1, 2, 3);
        Vector b = a.copy();
        assertNotSame(a, b);
        assertEquals(a, b);
    }
}
