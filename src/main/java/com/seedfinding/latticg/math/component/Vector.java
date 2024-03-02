package com.seedfinding.latticg.math.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntToDoubleFunction;

/**
 * A vector with double elements
 */
public final class Vector {

    double[] numbers;
    // to support views over a backing array, for internal use by Matrix
    int startPos = 0;
    int step = 1;
    private int dimension;

    /**
     * Constructs the zero vector of the given dimension
     *
     * @param dimension The dimension of the vector to create
     */
    public Vector(int dimension) {
        this.dimension = dimension;
        this.numbers = new double[this.dimension];
    }

    /**
     * Constructs a vector with the given elements
     *
     * @param numbers The elements of the vector
     */
    public Vector(double... numbers) {
        this.numbers = numbers;
        this.dimension = this.numbers.length;
    }

    /**
     * Generates a vector with the given dimension, applying a function to the index to compute the value of each
     * element
     *
     * @param dimension The dimension of the vector
     * @param generator A function accepting the index and producing the desired value for that index
     */
    public Vector(int dimension, IntToDoubleFunction generator) {
        this.dimension = dimension;
        this.numbers = new double[this.dimension];
        Arrays.setAll(numbers, generator);
    }

    // for internal use by Matrix
    static Vector createView(double[] array, int dimension, int startPos, int step) {
        Vector vec = new Vector(array);
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
    public double get(int i) {
        if (i < 0 || i >= dimension) {
            throw new IndexOutOfBoundsException("Index " + i + ", dimension " + dimension);
        }
        return this.numbers[step * i + startPos];
    }

    /**
     * Sets the element at the given index in the vector
     *
     * @param i     The index
     * @param value The value to put in that index
     * @throws IndexOutOfBoundsException If {@code i} is out of bounds
     */
    public void set(int i, double value) {
        if (i < 0 || i >= dimension) {
            throw new IndexOutOfBoundsException("Index " + i + ", dimension " + dimension);
        }
        this.numbers[step * i + startPos] = value;
    }

    /**
     * Returns the magnitude (norm) of the vector.
     *
     * {@link #magnitudeSq()} should be used instead of this method where possible
     *
     * @return The magnitude of this vector
     */
    public double magnitude() {
        return Math.sqrt(magnitudeSq());
    }

    /**
     * Returns the square of the magnitude (norm) of this vector
     *
     * @return The square of the magnitude of this vector
     */
    public double magnitudeSq() {
        double magnitude = 0.0D;

        for (int i = 0; i < this.getDimension(); i++) {
            magnitude += this.get(i) * this.get(i);
        }

        return magnitude;
    }

    /**
     * Returns whether this vector is the zero vector
     *
     * @return Whether this vector is the zero vector
     */
    public boolean isZero() {
        for (int i = 0; i < this.getDimension(); i++) {
            if (this.get(i) != 0.0D) return false;
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
    public Vector add(Vector a) {
        return copy().addAndSet(a);
    }

    /**
     * Subtracts the given vector from this vector, stores the result in a new vector and returns that vector
     *
     * @param a The vector to subtract
     * @return A new vector containing the result
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public Vector subtract(Vector a) {
        return copy().subtractAndSet(a);
    }

    /**
     * Multiplies this vector by the given scalar, stores the result in a new vector and returns that vector
     *
     * @param scalar The scalar to multiply by
     * @return A new vector containing the result
     */
    public Vector multiply(double scalar) {
        return copy().multiplyAndSet(scalar);
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
    public Vector multiply(Matrix m) {
        if (this.getDimension() != m.getRowCount()) {
            throw new IllegalArgumentException("Vector dimension should equal the number of matrix rows");
        }

        Vector v = new Vector(m.getColumnCount());

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
    public Vector divide(double scalar) {
        return copy().divideAndSet(scalar);
    }

    /**
     * Swaps the two numbers at the given indices, stores the result in a new vector and returns that matrix
     *
     * @param i The row to swap with {@code j}
     * @param j The row to swap with {@code i}
     * @return A new vector containing the result
     * @throws IndexOutOfBoundsException If {@code i} or {@code j} is out of bounds
     */
    public Vector swapNums(int i, int j) {
        return copy().swapNumsAndSet(i, j);
    }

    /**
     * Adds the given vector to this vector, modifying this vector
     *
     * @param a The vector to add to this vector
     * @return This vector
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public Vector addAndSet(Vector a) {
        assertSameDimension(a);

        for (int i = 0; i < this.getDimension(); i++) {
            this.set(i, this.get(i) + a.get(i));
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
    public Vector subtractAndSet(Vector a) {
        assertSameDimension(a);

        for (int i = 0; i < this.getDimension(); i++) {
            this.set(i, this.get(i) - a.get(i));
        }

        return this;
    }

    /**
     * Multiplies this vector by the given scalar, modifying this vector
     *
     * @param scalar The scalar to multiply this vector by
     * @return This vector
     */
    public Vector multiplyAndSet(double scalar) {
        for (int i = 0; i < this.getDimension(); i++) {
            this.set(i, this.get(i) * scalar);
        }

        return this;
    }

    /**
     * Divides this vector by the given scalar, modifying this vector
     *
     * @param scalar The scalar to divide this vector by
     * @return This vector
     */
    public Vector divideAndSet(double scalar) {
        for (int i = 0; i < this.getDimension(); i++) {
            this.set(i, this.get(i) / scalar);
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
    public Vector swapNumsAndSet(int i, int j) {
        double temp = this.get(i);
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
    public double dot(Vector v) {
        assertSameDimension(v);

        double dot = 0.0D;

        for (int i = 0; i < this.getDimension(); i++) {
            dot += this.get(i) * v.get(i);
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
    public double gramSchmidtCoefficient(Vector v) {
        return this.dot(v) / v.magnitudeSq();
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
    public Vector projectOnto(Vector v) {
        return v.multiply(this.gramSchmidtCoefficient(v));
    }

    /**
     * Creates a copy of this vector
     *
     * @return A copy of this vector
     */
    public Vector copy() {
        if (step == 1) {
            return new Vector(Arrays.copyOfRange(numbers, startPos, startPos + dimension));
        } else {
            Vector dest = new Vector(this.getDimension());
            for (int i = 0; i < dest.getDimension(); i++) {
                dest.set(i, this.get(i));
            }
            return dest;
        }
    }

    private void assertSameDimension(Vector other) {
        if (other.dimension != this.dimension) {
            throw new IllegalArgumentException("The other vector is not the same dimension");
        }
    }

    /**
     * Returns whether this vector has the same dimension as the given vector, and all elements of this vector are
     * within {@code tolerance} of the corresponding elements in the given vector
     *
     * @param other     The vector to test against
     * @param tolerance The maximum amount each element is allowed to differ
     * @return Whether this vector is close enough to the given vector
     */
    public boolean equals(Vector other, double tolerance) {
        if (other.dimension != this.dimension) {
            return false;
        }
        for (int i = 0; i < dimension; i++) {
            if (Math.abs(other.get(i) - this.get(i)) > tolerance) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (int i = 0; i < dimension; i++) {
            h = 31 * h + Double.hashCode(get(i));
        }
        return h;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null || other.getClass() != Vector.class) return false;
        Vector that = (Vector) other;
        return this.equals(that, 0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        for (int i = 0; i < this.getDimension(); i++) {
            sb.append(this.get(i)).append(i == this.getDimension() - 1 ? "" : ", ");
        }

        return sb.append("}").toString();
    }

    public static Vector fromString(String str) {
        str = str.trim();
        if (!str.startsWith("{") || !str.endsWith("}")) {
            throw new IllegalArgumentException("Illegal Vector format");
        }

        List<Double> numbers = new ArrayList<>();
        int fracStart = 1;
        for (int fracEnd = str.indexOf(',', fracStart); fracEnd >= 0; fracStart = fracEnd + 1, fracEnd = str.indexOf(',', fracStart)) {
            numbers.add(Double.valueOf(str.substring(fracStart, fracEnd)));
        }
        numbers.add(Double.valueOf(str.substring(fracStart, str.length() - 1)));

        return new Vector(numbers.stream().mapToDouble(Double::doubleValue).toArray());
    }

    /**
     * Converts a {@link BigVector} to a vector of doubles
     *
     * @param v The {@code BigVector} to copy from
     * @return A vector of doubles with the same values as the input vector, where possible
     */
    public static Vector fromBigVector(BigVector v) {
        Vector p = new Vector(v.getDimension());

        for (int i = 0; i < p.getDimension(); i++) {
            p.set(i, v.get(i).toDouble());
        }

        return p;
    }
}
