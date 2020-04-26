package randomreverser.math.component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * Can store any rational number.
 *
 * <p>Simplification rules:
 * <ul>
 *     <li>If the numerator is zero, then the denominator is one</li>
 *     <li>The numerator holds the sign of the fraction, and the denominator is always positive</li>
 *     <li>If the numerator is not zero, then the numerator and the denominator are coprime</li>
 * </ul>
 *
 * <p>This class is immutable, meaning all operations return the result, and no operation can modify this fraction
 */
public final class BigFraction implements Comparable<BigFraction> {

    public static final BigFraction ZERO = new BigFraction(0);
    public static final BigFraction ONE = new BigFraction(1);
    public static final BigFraction HALF = new BigFraction(1, 2);
    public static final BigFraction MINUS_ONE = new BigFraction(-1);

    private BigInteger ntor;
    private BigInteger dtor;

    /**
     * Creates a {@code BigFraction} with the given numerator and denominator
     *
     * @param numerator The numerator
     * @param denominator The denominator
     * @throws ArithmeticException If {@code denominator} is zero
     */
    public BigFraction(BigInteger numerator, BigInteger denominator) {
        if (denominator.signum() == 0) {
            throw new ArithmeticException("/ by zero");
        }
        this.ntor = numerator;
        this.dtor = denominator;
        simplify();
    }

    /**
     * Creates a {@code BigFraction} with the given numerator and denominator
     *
     * @param numerator The numerator
     * @param denominator The denominator
     * @throws ArithmeticException If {@code denominator} is zero
     */
    public BigFraction(long numerator, long denominator) {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    /**
     * Creates a {@code BigFraction} representing the given integer
     *
     * @param numerator The integer to represent
     */
    public BigFraction(BigInteger numerator) {
        this(numerator, BigInteger.ONE);
    }

    /**
     * Creates a {@code BigFraction} representing the given integer
     *
     * @param numerator The integer to represent
     */
    public BigFraction(long numerator) {
        this(numerator, 1);
    }

    /**
     * Parses a string into a fraction.
     *
     * If the input string does not contain a "/", then the input is assumed to be an integer. Otherwise, the numerator
     * and denominator are separated by a single "/" character and possibly some whitespace
     *
     * @param str The string to parse
     * @return A fraction represented by the input string
     * @throws NumberFormatException If the input string has invalid format, or the denominator is zero
     */
    public static BigFraction parse(String str) {
        String[] parts = str.split("\\s*/\\s*", 2);
        try {
            if (parts.length == 1) {
                return new BigFraction(new BigInteger(parts[0]));
            } else {
                return new BigFraction(new BigInteger(parts[0]), new BigInteger(parts[1]));
            }
        } catch (NumberFormatException | ArithmeticException e) {
            throw new NumberFormatException("For input string: " + str);
        }
    }

    private void simplify() {
        if (ntor.signum() == 0) {
            dtor = BigInteger.ONE;
            return;
        }

        if (dtor.signum() == -1) {
            ntor = ntor.negate();
            dtor = dtor.negate();
        }

        BigInteger commonFactor = ntor.gcd(dtor);
        ntor = ntor.divide(commonFactor);
        dtor = dtor.divide(commonFactor);
    }

    /**
     * Returns the numerator of this fraction
     *
     * @return The numerator of this fraction
     */
    public BigInteger getNumerator() {
        return ntor;
    }

    /**
     * Returns the denominator of this fraction
     *
     * @return The denominator of this fraction
     */
    public BigInteger getDenominator() {
        return dtor;
    }

    /**
     * Converts this fraction to a {@link BigDecimal}, rounding with the given {@link MathContext} where necessary
     *
     * @param mc The math context for rounding
     * @return A {@code BigDecimal} approximation of this fraction
     */
    public BigDecimal toBigDecimal(MathContext mc) {
        return new BigDecimal(ntor).divide(new BigDecimal(dtor), mc);
    }

    private static final MathContext TO_DOUBLE_CONTEXT = MathContext.DECIMAL64;
    /**
     * Converts this fraction to a {@code double}, rounding where necessary
     *
     * @return A {@code double} approximation of this fraction
     */
    public double toDouble() {
        return toBigDecimal(TO_DOUBLE_CONTEXT).doubleValue();
    }

    /**
     * Adds this fraction to the other fraction
     *
     * @param other The fraction to add
     * @return The result of the sum
     */
    public BigFraction add(BigFraction other) {
        return new BigFraction(ntor.multiply(other.dtor).add(other.ntor.multiply(dtor)), dtor.multiply(other.dtor));
    }

    /**
     * Adds this fraction to the given integer
     *
     * @param other The integer to add
     * @return The result of the addition
     */
    public BigFraction add(BigInteger other) {
        return new BigFraction(ntor.add(other.multiply(dtor)), dtor);
    }

    /**
     * Adds this fraction to the given integer
     *
     * @param other The integer to add
     * @return The result of the addition
     */
    public BigFraction add(long other) {
        return add(BigInteger.valueOf(other));
    }

    /**
     * Subtracts the other fraction from this fraction
     *
     * @param other The fraction to subtract
     * @return The result of the subtraction
     */
    public BigFraction subtract(BigFraction other) {
        return new BigFraction(ntor.multiply(other.dtor).subtract(other.ntor.multiply(dtor)), dtor.multiply(other.dtor));
    }

    /**
     * Subtracts the given integer from this fraction
     *
     * @param other The integer to subtract
     * @return The result of the addition
     */
    public BigFraction subtract(BigInteger other) {
        return new BigFraction(ntor.subtract(other.multiply(dtor)), dtor);
    }

    /**
     * Subtracts the given integer from this fraction
     *
     * @param other The integer to subtract
     * @return The result of the addition
     */
    public BigFraction subtract(long other) {
        return subtract(BigInteger.valueOf(other));
    }

    /**
     * Multiplies this fraction with the other fraction
     *
     * @param other The fraction to multiply by
     * @return The result of the multiplication
     */
    public BigFraction multiply(BigFraction other) {
        return new BigFraction(ntor.multiply(other.ntor), dtor.multiply(other.dtor));
    }

    /**
     * Multiplies this fraction with the given integer
     *
     * @param other The integer to multiply by
     * @return The result of the multiplication
     */
    public BigFraction multiply(BigInteger other) {
        return new BigFraction(ntor.multiply(other), dtor);
    }

    /**
     * Multiplies this fraction with the given integer
     *
     * @param other The integer to multiply by
     * @return The result of the multiplication
     */
    public BigFraction multiply(long other) {
        return multiply(BigInteger.valueOf(other));
    }

    /**
     * Divides this fraction by the other fraction
     *
     * @param other The fraction to divide by
     * @return The result of the division
     * @throws ArithmeticException If {@code other} is zero
     */
    public BigFraction divide(BigFraction other) {
        return new BigFraction(ntor.multiply(other.dtor), dtor.multiply(other.ntor));
    }

    /**
     * Divides this fraction by the given integer
     *
     * @param other The integer to divide by
     * @return The result of the division
     * @throws ArithmeticException If {@code other} is zero
     */
    public BigFraction divide(BigInteger other) {
        return new BigFraction(ntor, dtor.multiply(other));
    }

    /**
     * Divides this fraction by the given integer
     *
     * @param other The integer to divide by
     * @return The result of the division
     * @throws ArithmeticException If {@code other} is zero
     */
    public BigFraction divide(long other) {
        return divide(BigInteger.valueOf(other));
    }

    /**
     * Returns -{@code this}
     *
     * @return The result of the negation
     */
    public BigFraction negate() {
        return new BigFraction(ntor.negate(), dtor);
    }

    /**
     * Returns {@code this}<sup>-1</sup>
     *
     * @return The result of the reciprocation
     * @throws ArithmeticException If this fraction is zero
     */
    public BigFraction reciprocal() {
        return new BigFraction(dtor, ntor);
    }

    /**
     * Returns the largest integer {@code k} such that {@code k <= this}
     *
     * @return The floor of this fraction
     */
    public BigInteger floor() {
        if (dtor.equals(BigInteger.ONE)) {
            return ntor;
        } else if (ntor.signum() == -1) {
            return ntor.divide(dtor).subtract(BigInteger.ONE);
        } else {
            return ntor.divide(dtor);
        }
    }

    /**
     * Returns the smallest integer {@code k} such that {@code k >= this}
     *
     * @return The floor of this fraction
     */
    public BigInteger ceil() {
        if (dtor.equals(BigInteger.ONE)) {
            return ntor;
        } else if (ntor.signum() == 1) {
            return ntor.divide(dtor).add(BigInteger.ONE);
        } else {
            return ntor.divide(dtor);
        }
    }

    /**
     * Returns the closest integer to this fraction. In the case of there being 2 closest integers, returns the higher
     * of the two
     *
     * @return This fraction rounded to the nearest integer
     */
    public BigInteger round() {
        return add(HALF).floor();
    }

    /**
     * Returns the sign of this fraction
     *
     * @return -1 if negative, 0 if zero, 1 if positive
     */
    public int signum() {
        return ntor.signum();
    }

    /**
     * Returns the absolute value of this fraction
     *
     * @return The absolute value of this fraction, always non-negative.
     */
    public BigFraction abs() {
        return ntor.signum() == -1 ? negate() : this;
    }

    @Override
    public int compareTo(BigFraction other) {
        return ntor.multiply(other.dtor).compareTo(other.ntor.multiply(dtor));
    }

    @Override
    public int hashCode() {
        return ntor.hashCode() + 31 * dtor.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null || other.getClass() != BigFraction.class) return false;
        BigFraction that = (BigFraction) other;
        return this.ntor.equals(that.ntor) && this.dtor.equals(that.dtor);
    }

    @Override
    public String toString() {
        if (dtor.equals(BigInteger.ONE)) {
            return ntor.toString();
        }
        return ntor + "/" + dtor;
    }

}
