package randomreverser.math.component;

import java.util.Arrays;

/**
 * A vector with {@link BigFraction} elements
 */
public final class BigVector {

    BigFraction[] numbers;
    private int dimension;

    // to support views over a backing array, for internal use by BigMatrix
    int startPos = 0;
    int step = 1;

    /**
     * Constructs the zero vector of the given dimension
     *
     * @param dimension The dimension of the vector to create
     */
    public BigVector(int dimension) {
        this.dimension = dimension;
        this.numbers = new BigFraction[this.dimension];
        Arrays.fill(numbers, BigFraction.ZERO);
    }

    /**
     * Constructs a vector with the given integer elements
     *
     * @param numbers The elements of the vector
     */
    public BigVector(long... numbers) {
        this(toBigFractions(numbers));
    }

    private static BigFraction[] toBigFractions(long[] numbers) {
        BigFraction[] fractions = new BigFraction[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            fractions[i] = new BigFraction(numbers[i]);
        }
        return fractions;
    }

    /**
     * Constructs a vector with the given elements
     *
     * @param numbers The elements of the vector
     */
    public BigVector(BigFraction... numbers) {
        this.dimension = numbers.length;
        this.numbers = numbers;
    }

    // for internal use by BigMatrix
    static BigVector createView(BigFraction[] array, int dimension, int startPos, int step) {
        BigVector vec = new BigVector(array);
        vec.dimension = dimension;
        vec.startPos = startPos;
        vec.step = step;
        return vec;
    }

    /**
     * Gets the dimension of the vector
     *
     * @return The dimension of the vector
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * Gets the element at the given index in the vector
     *
     * @param i The index
     * @return The element at the given index
     * @throws IndexOutOfBoundsException If {@code i} is out of bounds
     */
    public BigFraction get(int i) {
        if (i < 0 || i >= dimension) {
            throw new IndexOutOfBoundsException("Index " + i + ", dimension " + dimension);
        }
        return this.numbers[step * i + startPos];
    }

    /**
     * Sets the element at the given index in the vector
     *
     * @param i The index
     * @param value The value to put in that index
     * @throws IndexOutOfBoundsException If {@code i} is out of bounds
     */
    public void set(int i, BigFraction value) {
        if (i < 0 || i >= dimension) {
            throw new IndexOutOfBoundsException("Index " + i + ", dimension " + dimension);
        }
        this.numbers[step * i + startPos] = value;
    }

    /**
     * Returns the square of the magnitude (norm) of this vector.
     * Note: there is no {@code magnitude()} method in {@code BigVector}, as that can result in an irrational number.
     * If the un-squared magnitude is desired, the caller should convert the result to a decimal and square root it
     * themselves.
     *
     * @return The square of the magnitude of this vector
     */
    public BigFraction magnitudeSq() {
        BigFraction magnitude = BigFraction.ZERO;

        for(int i = 0; i < this.getDimension(); i++) {
            magnitude = magnitude.add(this.get(i).multiply(this.get(i)));
        }

        return magnitude;
    }

    /**
     * Returns whether this vector is the zero vector
     *
     * @return Whether this vector is the zero vector
     */
    public boolean isZero() {
        for(int i = 0; i < this.getDimension(); i++) {
            if(this.get(i).signum() != 0) return false;
        }

        return true;
    }

    /**
     * Adds the given vector to this vector, stores the result in a new vector and returns that vector
     *
     * @param a The vector to add
     * @return A new vector containing the result
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigVector add(BigVector a) {
        return copy().addEquals(a);
    }

    /**
     * Subtracts the given vector from this vector, stores the result in a new vector and returns that vector
     *
     * @param a The vector to subtract
     * @return A new vector containing the result
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigVector subtract(BigVector a) {
        return copy().subtractEquals(a);
    }

    /**
     * Multiplies this vector by the given scalar, stores the result in a new vector and returns that vector
     *
     * @param scalar The scalar to multiply by
     * @return A new vector containing the result
     */
    public BigVector multiply(BigFraction scalar) {
        return copy().multiplyEquals(scalar);
    }

    /**
     * Computes {@code this * m}, interpreting this vector as a row vector, stores the result in a new vector and
     * returns that vector
     *
     * @param m The matrix to right-multiply by
     * @return A new vector containing the result
     * @throws IllegalArgumentException If the dimension of this vector is not equal to the number of rows in the given
     *                                  matrix
     */
    public BigVector multiply(BigMatrix m) {
        if (this.getDimension() != m.getRowCount()) {
            throw new IllegalArgumentException("Vector dimension should equal the number of matrix rows");
        }

        BigVector v = new BigVector(m.getColumnCount());

        for (int i = 0; i < v.getDimension(); i++) {
            v.set(i, this.dot(m.getColumn(i)));
        }

        return v;
    }

    /**
     * Divides this vector by the given scalar, stores the result in a new vector and returns that vector
     *
     * @param scalar The scalar to divide by
     * @return A new vector containing the result
     */
    public BigVector divide(BigFraction scalar) {
        return copy().divideEquals(scalar);
    }

    /**
     * Swaps the two numbers at the given indices, stores the result in a new vector and returns that matrix
     *
     * @param i The row to swap with {@code j}
     * @param j The row to swap with {@code i}
     * @return A new vector containing the result
     * @throws IndexOutOfBoundsException If {@code i} or {@code j} is out of bounds
     */
    public BigVector swapNums(int i, int j) {
        return copy().swapNumsEquals(i, j);
    }

    /**
     * Adds the given vector to this vector, modifying this vector
     *
     * @param a The vector to add to this vector
     * @return This vector
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigVector addEquals(BigVector a) {
        assertSameDimension(a);

        for(int i = 0; i < this.getDimension(); i++) {
            this.set(i, this.get(i).add(a.get(i)));
        }

        return this;
    }

    /**
     * Subtracts the given vector from this vector, modifying this vector
     *
     * @param a The vector to subtract from this vector
     * @return This vector
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigVector subtractEquals(BigVector a) {
        assertSameDimension(a);

        for(int i = 0; i < this.getDimension(); i++) {
            this.set(i, this.get(i).subtract(a.get(i)));
        }

        return this;
    }

    /**
     * Multiplies this vector by the given scalar, modifying this vector
     *
     * @param scalar The scalar to multiply this vector by
     * @return This vector
     */
    public BigVector multiplyEquals(BigFraction scalar) {
        for(int i = 0; i < this.getDimension(); i++) {
            this.set(i, this.get(i).multiply(scalar));
        }

        return this;
    }

    /**
     * Divides this vector by the given scalar, modifying this vector
     *
     * @param scalar The scalar to divide this vector by
     * @return This vector
     */
    public BigVector divideEquals(BigFraction scalar) {
        for(int i = 0; i < this.getDimension(); i++) {
            this.set(i, this.get(i).divide(scalar));
        }

        return this;
    }

    /**
     * Swaps the two numbers at the given indices, modifying the vector
     *
     * @param i The row to swap with {@code j}
     * @param j The row to swap with {@code i}
     * @return This vector
     * @throws IndexOutOfBoundsException If {@code i} or {@code j} is out of bounds
     */
    public BigVector swapNumsEquals(int i, int j) {
        BigFraction temp = this.get(i);
        this.set(i, this.get(j));
        this.set(j, temp);
        return this;
    }

    /**
     * Calculates the dot product of this vector and the given vector
     *
     * @param v The vector to find the dot product with
     * @return The dot product of this vector and the given vector
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigFraction dot(BigVector v) {
        assertSameDimension(v);

        BigFraction dot = BigFraction.ZERO;

        for(int i = 0; i < this.getDimension(); i++) {
            dot = dot.add(this.get(i).multiply(v.get(i)));
        }

        return dot;
    }

    /**
     * Returns the Gram-Schmidt coefficient when this vector is projected onto the given vector, that is, the ratio of
     * the length of the projection of this vector onto the given vector, to the given vector's length. In other words,
     * multiplying the given vector by the Gram-Schmidt coefficient gives the projection of this vector onto the given
     * vector
     *
     * @param v The vector to project onto
     * @return The Gram-Schmidt coefficient when this vector is projected onto the given vector
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigFraction gramSchmidtCoefficient(BigVector v) {
        return this.dot(v).divide(v.magnitudeSq());
    }

    /**
     * Calculates the projection of this vector onto the given vector, stores the result in a new vector and returns
     * that vector
     *
     * @param v The vector to project onto
     * @return A new vector containing the result
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigVector projectOnto(BigVector v) {
        return v.multiply(this.gramSchmidtCoefficient(v));
    }

    /**
     * Creates a copy of this vector
     *
     * @return A copy of this vector
     */
    public BigVector copy() {
        if (step == 1) {
            return new BigVector(Arrays.copyOfRange(numbers, startPos, startPos + dimension));
        }

        BigVector v = new BigVector(this.getDimension());
        for (int i = 0; i < v.getDimension(); i++) {
            v.set(i, this.get(i));
        }
        return v;
    }

    private void assertSameDimension(BigVector other) {
        if (other.dimension != this.dimension) {
            throw new IllegalArgumentException("The other vector is not the same dimension");
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (int i = 0; i < dimension; i++) {
            h = 31 * h + get(i).hashCode();
        }
        return h;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null || other.getClass() != BigVector.class) return false;
        BigVector that = (BigVector) other;
        if (this.dimension != that.dimension) {
            return false;
        }
        for (int i = 0; i < dimension; i++) {
            if (!this.get(i).equals(that.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        for(int i = 0; i < this.getDimension(); i++) {
            sb.append(this.get(i))
                    .append(i == this.getDimension() - 1 ? "" : ", ");
        }

        return sb.append("}").toString();
    }

    /**
     * Parses a string in wolfram-style vector notation
     *
     * @param raw The string in wolfram-style vector notation
     * @return The parsed vector
     * @throws IllegalArgumentException If the input is malformed
     * @throws NumberFormatException If the input is malformed
     */
    public static BigVector fromString(String raw) {
        raw = raw.replaceAll("\\s+","");

        String[] data = raw.split(",");
        BigVector v = new BigVector(data.length);

        for(int i = 0; i < data.length; i++) {
            v.set(i, BigFraction.parse(data[i]));
        }

        return v;
    }
}
