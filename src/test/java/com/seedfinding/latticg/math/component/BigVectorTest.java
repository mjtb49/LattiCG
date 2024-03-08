package com.seedfinding.latticg.math.component;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BigVectorTest {

    @Test
    public void testGetDimension0() {
        assertEquals(0, new BigVector(0).getDimension());
    }

    @Test
    public void testGetDimension3() {
        assertEquals(3, new BigVector(3).getDimension());
    }

    @Test
    public void testGet() {
        assertEquals(new BigFraction(4), new BigVector(1, 2, 3, 4, 5).get(3));
    }

    @Test
    public void testMagnitudeSq() {
        assertEquals(new BigFraction(55), new BigVector(1, 2, 3, 4, 5).magnitudeSq());
    }

    @Test
    public void testIsZeroTrue() {
        assertTrue(new BigVector(0, 0, 0, 0, 0).isZero());
    }

    @Test
    public void testIsZeroFalse1() {
        assertFalse(new BigVector(1, 2, 3, 4, 5).isZero());
    }

    @Test
    public void testIsZeroFalse2() {
        assertFalse(new BigVector(1, -1).isZero());
    }

    @Test
    public void testAdd() {
        BigVector a = new BigVector(1, 2, 3, 4, 5);
        BigVector b = new BigVector(5, 3, 6, -4, -1);
        assertEquals(new BigVector(6, 5, 9, 0, 4), a.add(b));
        assertEquals(new BigVector(1, 2, 3, 4, 5), a);
    }

    @Test
    public void testAddFail() {
        assertThrows(IllegalArgumentException.class, ()-> new BigVector(1, 2).add(new BigVector(3, 4, 5)));
    }

    @Test
    public void testSubtract() {
        BigVector a = new BigVector(1, 2, 3, 4, 5);
        BigVector b = new BigVector(-5, -3, -6, 4, 1);
        assertEquals(new BigVector(6, 5, 9, 0, 4), a.subtract(b));
        assertEquals(new BigVector(1, 2, 3, 4, 5), a);
    }

    @Test
    public void testSubtractFail() {
        assertThrows(IllegalArgumentException.class, () -> new BigVector(1, 2).subtract(new BigVector(3, 4, 5)));
    }

    @Test
    public void testMultiplyScalar() {
        BigVector a = new BigVector(1, 2, 3, 4, 5);
        assertEquals(new BigVector(2, 4, 6, 8, 10), a.multiply(new BigFraction(2)));
        assertEquals(new BigVector(1, 2, 3, 4, 5), a);
    }

    @Test
    public void testMultiplyMatrix1() {
        BigVector a = new BigVector(2, 3);
        BigMatrix b = BigMatrixUtil.fromString("{{5, 7}, {11, 13}}");
        assertEquals(new BigVector(43, 53), a.multiply(b));
        assertEquals(new BigVector(2, 3), a);
    }

    @Test
    public void testMultiplyMatrix2() {
        BigVector a = new BigVector(2, 3, 5);
        BigMatrix b = BigMatrixUtil.fromString("{{7, 11}, {13, 17}, {19, 23}}");
        assertEquals(new BigVector(148, 188), a.multiply(b));
        assertEquals(new BigVector(2, 3, 5), a);
    }

    @Test
    public void testMultiplyMatrixFail() {
        assertThrows(IllegalArgumentException.class, () -> {
            BigVector a = new BigVector(2, 3, 5);
            BigMatrix b = BigMatrixUtil.fromString("{{7, 11, 13}, {17, 19, 23}}");
            a.multiply(b);
        });
    }

    @Test
    public void testDivide() {
        BigVector a = new BigVector(2, 4);
        assertEquals(new BigVector(1, 2), a.divide(new BigFraction(2)));
        assertEquals(new BigVector(2, 4), a);
    }


    @Test
    public void testSwapNums() {
        BigVector a = new BigVector(1, 2, 3);
        assertEquals(new BigVector(1, 3, 2), a.swapNums(1, 2));
        assertEquals(new BigVector(1, 2, 3), a);
    }

    @Test
    public void testAddAndSet() {
        BigVector a = new BigVector(1, 2, 3, 4, 5);
        BigVector b = new BigVector(5, 3, 6, -4, -1);
        BigVector result = a.addAndSet(b);
        assertSame(result, a);
        assertEquals(new BigVector(6, 5, 9, 0, 4), a);
    }

    @Test
    public void testAddAndSetFail() {
        assertThrows(IllegalArgumentException.class, () -> new BigVector(1, 2).addAndSet(new BigVector(3, 4, 5)));
    }

    @Test
    public void testSubtractAndSet() {
        BigVector a = new BigVector(1, 2, 3, 4, 5);
        BigVector b = new BigVector(-5, -3, -6, 4, 1);
        BigVector result = a.subtractAndSet(b);
        assertSame(result, a);
        assertEquals(new BigVector(6, 5, 9, 0, 4), a);
    }

    @Test
    public void testSubtractAndSetFail() {
        assertThrows(IllegalArgumentException.class, () -> new BigVector(1, 2).subtractAndSet(new BigVector(3, 4, 5)));
    }

    @Test
    public void testMultiplyAndSet() {
        BigVector a = new BigVector(1, 2, 3, 4, 5);
        BigVector result = a.multiplyAndSet(new BigFraction(2));
        assertSame(result, a);
        assertEquals(new BigVector(2, 4, 6, 8, 10), a);
    }

    @Test
    public void divideAndSet() {
        BigVector a = new BigVector(2, 4);
        BigVector result = a.divideAndSet(new BigFraction(2));
        assertSame(result, a);
        assertEquals(new BigVector(1, 2), a);
    }

    @Test
    public void testSwapNumsAndSet() {
        BigVector a = new BigVector(1, 2, 3);
        BigVector result = a.swapNumsAndSet(1, 2);
        assertSame(result, a);
        assertEquals(new BigVector(1, 3, 2), a);
    }

    @Test
    public void testDot() {
        BigVector a = new BigVector(2, 3, 5);
        BigVector b = new BigVector(7, 11, 13);
        assertEquals(new BigFraction(112), a.dot(b));
        assertEquals(new BigFraction(112), b.dot(a));
    }

    @Test
    public void testDotFail() {
        assertThrows(IllegalArgumentException.class, () -> new BigVector(1, 2).dot(new BigVector(3, 4, 5)));
    }

    @Test
    public void testGramSchmidtCoefficient() {
        BigVector a = new BigVector(7, 11, 13);
        BigVector b = new BigVector(2, 3, 5);
        assertEquals(new BigFraction(56, 19), a.gramSchmidtCoefficient(b));
    }

    @Test
    public void testGramSchmidtCoefficientFail() {
        assertThrows(IllegalArgumentException.class, () -> new BigVector(1, 2).gramSchmidtCoefficient(new BigVector(3, 4, 5)));
    }

    @Test
    public void testProjectOnto() {
        BigVector a = new BigVector(7, 11, 13);
        BigVector b = new BigVector(2, 3, 5);
        BigVector expected = new BigVector(
            new BigFraction(112, 19),
            new BigFraction(168, 19),
            new BigFraction(280, 19)
        );
        assertEquals(expected, a.projectOnto(b));
    }

    @Test
    public void testProjectOntoFail() {
        assertThrows(IllegalArgumentException.class, () -> new BigVector(1, 2).projectOnto(new BigVector(3, 4, 5)));
    }

    @Test
    public void testCopy() {
        BigVector a = new BigVector(1, 2, 3);
        BigVector b = a.copy();
        assertNotSame(a, b);
        assertEquals(a, b);
    }
}
