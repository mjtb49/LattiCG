package randomreverser.math.component;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static org.junit.Assert.*;

public class BigFractionTest {

    @Test(expected = ArithmeticException.class)
    public void testConstructDivideZero() {
        new BigFraction(1, 0);
    }

    @Test
    public void testConstructZero() {
        BigFraction zero = new BigFraction(0, 47);
        assertEquals(BigInteger.ZERO, zero.getNumerator());
        assertEquals(BigInteger.ONE, zero.getDenominator());
    }

    @Test
    public void testConstructNegativeDenominator1() {
        BigFraction minusHalf = new BigFraction(1, -2);
        assertEquals(BigInteger.valueOf(-1), minusHalf.getNumerator());
        assertEquals(BigInteger.valueOf(2), minusHalf.getDenominator());
    }

    @Test
    public void testConstructNegativeDenominator2() {
        BigFraction half = new BigFraction(-1, -2);
        assertEquals(BigInteger.ONE, half.getNumerator());
        assertEquals(BigInteger.valueOf(2), half.getDenominator());
    }

    @Test
    public void testConstructSimplify() {
        BigFraction half = new BigFraction(2, 4);
        assertEquals(BigInteger.ONE, half.getNumerator());
        assertEquals(BigInteger.valueOf(2), half.getDenominator());
    }

    @Test
    public void testToBigDecimal() {
        assertEquals(BigDecimal.valueOf(0.5), BigFraction.HALF.toBigDecimal(MathContext.UNLIMITED));
    }

    @Test
    public void testAddFractionWithFraction() {
        BigFraction a = new BigFraction(7, 11);
        BigFraction b = new BigFraction(13, 17);
        assertEquals(new BigFraction(262, 187), a.add(b));
    }

    @Test
    public void testAddFractionWithInteger() {
        BigFraction a = new BigFraction(7, 11);
        assertEquals(new BigFraction(150, 11), a.add(13));
    }

    @Test
    public void testSubtractFractionWithFraction() {
        BigFraction a = new BigFraction(7, 11);
        BigFraction b = new BigFraction(13, 17);
        assertEquals(new BigFraction(-24, 187), a.subtract(b));
    }

    @Test
    public void testSubtractFractionWithInteger() {
        BigFraction a = new BigFraction(7, 11);
        assertEquals(new BigFraction(-136, 11), a.subtract(13));
    }

    @Test
    public void testMultiplyFractionWithFraction() {
        BigFraction a = new BigFraction(7, 11);
        BigFraction b = new BigFraction(13, 17);
        assertEquals(new BigFraction(91, 187), a.multiply(b));
    }

    @Test
    public void testMultiplyFractionWithInteger() {
        BigFraction a = new BigFraction(7, 11);
        assertEquals(new BigFraction(91, 11), a.multiply(13));
    }

    @Test
    public void testDivisionFractionWithFraction() {
        BigFraction a = new BigFraction(7, 11);
        BigFraction b = new BigFraction(13, 17);
        assertEquals(new BigFraction(119, 143), a.divide(b));
    }

    @Test
    public void testDivisionFractionWithInteger() {
        BigFraction a = new BigFraction(7, 11);
        assertEquals(new BigFraction(7, 143), a.divide(13));
    }

    @Test(expected = ArithmeticException.class)
    public void testDivisionByZero() {
        new BigFraction(1).divide(0);
    }

    @Test
    public void testNegateZero() {
        assertEquals(BigFraction.ZERO, BigFraction.ZERO.negate());
    }

    @Test
    public void testNegatePositive() {
        assertEquals(BigFraction.MINUS_ONE, BigFraction.ONE.negate());
    }

    @Test
    public void testNegateNegative() {
        assertEquals(BigFraction.ONE, BigFraction.MINUS_ONE.negate());
    }

    @Test
    public void testReciprocal() {
        assertEquals(BigFraction.HALF, new BigFraction(2).reciprocal());
    }

    @Test(expected = ArithmeticException.class)
    public void testReciprocalZero() {
        BigFraction.ZERO.reciprocal();
    }

    @Test
    public void testFloorZero() {
        assertEquals(BigInteger.ZERO, BigFraction.ZERO.floor());
    }

    @Test
    public void testFloorPositiveInteger() {
        assertEquals(BigInteger.ONE, BigFraction.ONE.floor());
    }

    @Test
    public void testFloorNegativeInteger() {
        assertEquals(BigInteger.valueOf(-1), BigFraction.MINUS_ONE.floor());
    }

    @Test
    public void testFloorPositive() {
        assertEquals(BigInteger.ONE, new BigFraction(3, 2).floor());
    }

    @Test
    public void testFloorNegative() {
        assertEquals(BigInteger.valueOf(-2), new BigFraction(-3, 2).floor());
    }

    @Test
    public void testCeilZero() {
        assertEquals(BigInteger.ZERO, BigFraction.ZERO.ceil());
    }

    @Test
    public void testCeilPositiveInteger() {
        assertEquals(BigInteger.ONE, BigFraction.ONE.ceil());
    }

    @Test
    public void testCeilNegativeInteger() {
        assertEquals(BigInteger.valueOf(-1), BigFraction.MINUS_ONE.ceil());
    }

    @Test
    public void testCeilPositive() {
        assertEquals(BigInteger.valueOf(2), new BigFraction(3, 2).ceil());
    }

    @Test
    public void testCeilNegative() {
        assertEquals(BigInteger.valueOf(-1), new BigFraction(-3, 2).ceil());
    }

    @Test
    public void testRoundZero() {
        assertEquals(BigInteger.ZERO, BigFraction.ZERO.round());
    }

    @Test
    public void testRoundPositiveInteger() {
        assertEquals(BigInteger.ONE, BigFraction.ONE.round());
    }

    @Test
    public void testRoundNegativeInteger() {
        assertEquals(BigInteger.valueOf(-1), BigFraction.MINUS_ONE.round());
    }

    @Test
    public void testRoundHalf() {
        assertEquals(BigInteger.ONE, BigFraction.HALF.round());
    }

    @Test
    public void testRoundMinusHalf() {
        assertEquals(BigInteger.ZERO, new BigFraction(-1, 2).round());
    }

    @Test
    public void testRoundingLessThanHalfPositive() {
        assertEquals(BigInteger.ONE, new BigFraction(4, 3).round());
    }

    @Test
    public void testRoundingMoreThanHalfPositive() {
        assertEquals(BigInteger.valueOf(2), new BigFraction(5, 3).round());
    }

    @Test
    public void testRoundingLessThanHalfNegative() {
        assertEquals(BigInteger.valueOf(-2), new BigFraction(-5, 3).round());
    }

    @Test
    public void testRoundingMoreThanHalfNegative() {
        assertEquals(BigInteger.valueOf(-1), new BigFraction(-4, 3).round());
    }

    @Test
    public void testSignumZero() {
        assertEquals(0, BigFraction.ZERO.signum());
    }

    @Test
    public void testSignumPositive() {
        assertEquals(1, BigFraction.HALF.signum());
    }

    @Test
    public void testSignumNegative() {
        assertEquals(-1, new BigFraction(-47).signum());
    }

    @Test
    public void testAbsZero() {
        assertEquals(BigFraction.ZERO, BigFraction.ZERO.abs());
    }

    @Test
    public void testAbsPositive() {
        assertEquals(BigFraction.ONE, BigFraction.ONE.abs());
    }

    @Test
    public void testAbsNegative() {
        assertEquals(BigFraction.ONE, BigFraction.MINUS_ONE.abs());
    }

    @Test
    public void testCompareEqual() {
        assertEquals(0, BigFraction.ONE.compareTo(new BigFraction(1)));
    }

    @Test
    public void testCompare() {
        BigFraction a = new BigFraction(7, 11);
        BigFraction b = new BigFraction(13, 17);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

}
