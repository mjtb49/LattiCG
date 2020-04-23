package randomreverser.math.component;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static randomreverser.util.MoreAssert.*;

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
        assertBigDecimalEquals(BigDecimal.valueOf(4), new BigVector(1, 2, 3, 4, 5).get(3));
    }

    @Test
    public void testMagnitudeSq() {
        assertBigDecimalEquals(BigDecimal.valueOf(55), new BigVector(1, 2, 3, 4, 5).magnitudeSq());
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

    @Test(expected = IllegalArgumentException.class)
    public void testAddFail() {
        new BigVector(1, 2).add(new BigVector(3, 4, 5));
    }

    @Test
    public void testSubtract() {
        BigVector a = new BigVector(1, 2, 3, 4, 5);
        BigVector b = new BigVector(-5, -3, -6, 4, 1);
        assertEquals(new BigVector(6, 5, 9, 0, 4), a.subtract(b));
        assertEquals(new BigVector(1, 2, 3, 4, 5), a);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubtractFail() {
        new BigVector(1, 2).subtract(new BigVector(3, 4, 5));
    }

    @Test
    public void testMultiplyScalar() {
        BigVector a = new BigVector(1, 2, 3, 4, 5);
        assertEquals(new BigVector(2, 4, 6, 8, 10), a.multiply(BigDecimal.valueOf(2)));
        assertEquals(new BigVector(1, 2, 3, 4, 5), a);
    }

    @Test
    public void testDivide() {
        BigVector a = new BigVector(2, 4);
        assertEquals(new BigVector(1, 2), a.divide(BigDecimal.valueOf(2)));
        assertEquals(new BigVector(2, 4), a);
    }

    @Test
    public void testAddEquals() {
        BigVector a = new BigVector(1, 2, 3, 4, 5);
        BigVector b = new BigVector(5, 3, 6, -4, -1);
        BigVector result = a.addEquals(b);
        assertSame(result, a);
        assertEquals(new BigVector(6, 5, 9, 0, 4), a);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEqualsFail() {
        new BigVector(1, 2).addEquals(new BigVector(3, 4, 5));
    }

    @Test
    public void testSubtractEquals() {
        BigVector a = new BigVector(1, 2, 3, 4, 5);
        BigVector b = new BigVector(-5, -3, -6, 4, 1);
        BigVector result = a.subtractEquals(b);
        assertSame(result, a);
        assertEquals(new BigVector(6, 5, 9, 0, 4), a);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubtractEqualsFail() {
        new BigVector(1, 2).subtractEquals(new BigVector(3, 4, 5));
    }

    @Test
    public void testMultiplyEquals() {
        BigVector a = new BigVector(1, 2, 3, 4, 5);
        BigVector result = a.multiplyEquals(BigDecimal.valueOf(2));
        assertSame(result, a);
        assertEquals(new BigVector(2, 4, 6, 8, 10), a);
    }

    @Test
    public void divideEquals() {
        BigVector a = new BigVector(2, 4);
        BigVector result = a.divideEquals(BigDecimal.valueOf(2));
        assertSame(result, a);
        assertEquals(new BigVector(1, 2), a);
    }

    @Test
    public void testDot() {
        BigVector a = new BigVector(2, 3, 5);
        BigVector b = new BigVector(7, 11, 13);
        assertBigDecimalEquals(BigDecimal.valueOf(112), a.dot(b));
        assertBigDecimalEquals(BigDecimal.valueOf(112), b.dot(a));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDotFail() {
        new BigVector(1, 2).dot(new BigVector(3, 4, 5));
    }

    @Test
    public void testGramSchmidtCoefficient() {
        BigVector a = new BigVector(7, 11, 13);
        BigVector b = new BigVector(2, 3, 5);
        assertBigDecimalEquals(BigDecimal.valueOf(56.0 / 19), a.gramSchmidtCoefficient(b), BigDecimal.valueOf(0.00000001));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGramSchmidtCoefficientFail() {
        new BigVector(1, 2).gramSchmidtCoefficient(new BigVector(3, 4, 5));
    }

    @Test
    public void testProjectOnto() {
        BigVector a = new BigVector(7, 11, 13);
        BigVector b = new BigVector(2, 3, 5);
        assertTrue(new BigVector(112.0 / 19, 168.0 / 19, 280.0 / 19).equals(a.projectOnto(b), BigDecimal.valueOf(0.00000001)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProjectOntoFail() {
        new BigVector(1, 2).projectOnto(new BigVector(3, 4, 5));
    }

    @Test
    public void testCopy() {
        BigVector a = new BigVector(1, 2, 3);
        BigVector b = a.copy();
        assertNotSame(a, b);
        assertEquals(a, b);
    }
}
