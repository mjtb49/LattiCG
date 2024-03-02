package com.seedfinding.latticg.math.component;

import java.util.Arrays;

/**
 * A matrix of {@link BigFraction} values
 */
public final class BigMatrix {

    private final BigFraction[] numbers;
    private final int rowCount;
    private final int columnCount;
    private final int underlyingColumnCount;
    private int startIndex = 0;

    /**
     * Constructs the zero matrix of the given size
     *
     * @param rowCount    The number of rows
     * @param columnCount The number of columns
     * @throws IllegalArgumentException If {@code rowCount} or {@code columnCount} isn't positive
     */
    public BigMatrix(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.underlyingColumnCount = columnCount;

        if (rowCount <= 0 || columnCount <= 0) {
            throw new IllegalArgumentException("Matrix dimensions cannot be less or equal to 0");
        }

        this.numbers = new BigFraction[rowCount * columnCount];
        Arrays.fill(numbers, BigFraction.ZERO);
    }

    /**
     * Constructs a matrix of the given size, using the given function to fill in each element.
     *
     * @param rowCount    The number of rows
     * @param columnCount The number of columns
     * @param gen         The function to call for each element of the matrix
     */
    public BigMatrix(int rowCount, int columnCount, DataProvider gen) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.underlyingColumnCount = columnCount;

        if (rowCount <= 0 || columnCount <= 0) {
            throw new IllegalArgumentException("Matrix dimensions cannot be less or equal to 0");
        }

        this.numbers = new BigFraction[rowCount * columnCount];
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                numbers[column + columnCount * row] = gen.getValue(row, column);
            }
        }
    }

    private BigMatrix(int rowCount, int columnCount, BigFraction[] numbers, int startIndex, int underlyingColumnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.numbers = numbers;
        this.startIndex = startIndex;
        this.underlyingColumnCount = underlyingColumnCount;
    }

    /**
     * Gets the number of rows in the matrix
     *
     * @return The number of rows in the matrix
     */
    public int getRowCount() {
        return this.rowCount;
    }

    /**
     * Gets the number of columns in the matrix
     *
     * @return The number of columns in the matrix
     */
    public int getColumnCount() {
        return this.columnCount;
    }

    /**
     * Returns whether this is a square matrix
     *
     * @return Whether this is a square matrix
     */
    public boolean isSquare() {
        return this.rowCount == this.columnCount;
    }

    /**
     * Gets a single value from the matrix
     *
     * @param row The row of the value to get
     * @param col The column of the value to get
     * @return The value in (row, col)
     * @throws IndexOutOfBoundsException If {@code row} or {@code col} is out of bounds
     */
    public BigFraction get(int row, int col) {
        if (row < 0 || row >= rowCount || col < 0 || col >= columnCount) {
            throw new IndexOutOfBoundsException("Index (" + row + ", " + col + "), size (" + rowCount + ", " + columnCount + ")");
        }
        return numbers[startIndex + col + underlyingColumnCount * row];
    }

    /**
     * Sets a single value in the matrix
     *
     * @param row   The row of the value to set
     * @param col   The column of the value to set
     * @param value The value to set
     * @throws IndexOutOfBoundsException If {@code row} or {@code col} is out of bounds
     */
    public void set(int row, int col, BigFraction value) {
        if (row < 0 || row >= rowCount || col < 0 || col >= columnCount) {
            throw new IndexOutOfBoundsException("Index (" + row + ", " + col + "), size (" + rowCount + ", " + columnCount + ")");
        }
        numbers[startIndex + col + underlyingColumnCount * row] = value;
    }

    /**
     * Gets a vector <i>view</i> of the row of the given index. Modifying this vector will modify the original matrix
     *
     * @param rowIndex The index of the row to get
     * @return A view of the row at {@code rowIndex}
     * @throws IndexOutOfBoundsException If {@code rowIndex} is out of bounds
     */
    public BigVector getRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rowCount) {
            throw new IndexOutOfBoundsException("Index " + rowIndex + ", size " + rowCount);
        }
        return BigVector.createView(numbers, columnCount, startIndex + rowIndex * underlyingColumnCount, 1);
    }

    /**
     * Gets a vector <i>view</i> of the column of the given index. Modifying this vector will modify the original matrix
     *
     * @param columnIndex The index of the column to get
     * @return A view of the column at {@code columnIndex}
     * @throws IndexOutOfBoundsException If {@code columnIndex} is out of bounds
     */
    public BigVector getColumn(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columnCount) {
            throw new IndexOutOfBoundsException("Index " + columnIndex + ", size " + columnCount);
        }
        return BigVector.createView(numbers, rowCount, startIndex + columnIndex, underlyingColumnCount);
    }

    /**
     * Sets the row at the given index to be the same as the given vector. Copies that vector into this matrix
     *
     * @param rowIndex The index of the row to set
     * @param newRow   The vector to set the row to
     * @throws IllegalArgumentException  If the dimension of the given vector is not equal to the number of columns in
     *                                   this matrix
     * @throws IndexOutOfBoundsException If {@code rowIndex} is out of bounds
     */
    public void setRow(int rowIndex, BigVector newRow) {
        if (newRow.getDimension() != this.columnCount) {
            throw new IllegalArgumentException("Invalid vector dimension, expected " + columnCount + ", got " + newRow.getDimension());
        }
        if (rowIndex < 0 || rowIndex >= rowCount) {
            throw new IndexOutOfBoundsException("Index " + rowIndex + ", size " + rowCount);
        }

        if (newRow.step == 1 && columnCount == underlyingColumnCount) {
            System.arraycopy(newRow.numbers, newRow.startPos, numbers, startIndex + rowIndex * columnCount, columnCount);
        } else {
            for (int i = 0; i < columnCount; i++) {
                set(rowIndex, i, newRow.get(i));
            }
        }
    }

    /**
     * Sets the column at the given index to be the same as the given vector. Copies that vector into this matrix
     *
     * @param columnIndex The index of the column to set
     * @param newColumn   The vector to set the column to
     * @throws IllegalArgumentException  If the dimension of the given vector is not equal to the number of rows in this
     *                                   matrix
     * @throws IndexOutOfBoundsException If {@code columnIndex} is out of bounds
     */
    public void setColumn(int columnIndex, BigVector newColumn) {
        if (newColumn.getDimension() != this.rowCount) {
            throw new IllegalArgumentException("Invalid vector dimension, expected " + rowCount + ", got " + newColumn.getDimension());
        }
        if (columnIndex < 0 || columnIndex >= columnCount) {
            throw new IndexOutOfBoundsException("Index " + columnIndex + ", size " + columnCount);
        }

        for (int i = 0; i < rowCount; i++) {
            set(i, columnIndex, newColumn.get(i));
        }
    }

    /**
     * Gets a submatrix <i>view</i> starting at the given position and of the given size. Modifying this matrix will
     * modify the original matrix
     *
     * @param startRow    The row of the top of the submatrix
     * @param startColumn The column on the left of the submatrix
     * @param rowCount    The number of rows in the submatrix
     * @param columnCount The number of columns in the submatrix
     * @return A view of the submatrix
     * @throws IndexOutOfBoundsException If {@code startRow}, {@code startColumn}, {@code rowCount} or
     *                                   {@code columnCount} is out of bounds
     */
    public BigMatrix submatrix(int startRow, int startColumn, int rowCount, int columnCount) {
        if (startRow < 0 || startColumn < 0 || rowCount <= 0 || columnCount <= 0 || startRow + rowCount > this.rowCount || startColumn + columnCount > this.columnCount) {
            throw new IllegalArgumentException(String.format("Illegal submatrix start (%d, %d) with size (%d, %d), size of original matrix (%d, %d)", startRow, startColumn, rowCount, columnCount, this.rowCount, this.columnCount));
        }
        return new BigMatrix(rowCount, columnCount, numbers, startIndex + startColumn + underlyingColumnCount * startRow, underlyingColumnCount);
    }

    /**
     * Adds the given matrix to this matrix, stores the result in a new matrix and returns that matrix
     *
     * @param m The matrix to add
     * @return A new matrix containing the result
     * @throws IllegalArgumentException If the given matrix is not the same size as this matrix
     */
    public BigMatrix add(BigMatrix m) {
        return copy().addAndSet(m);
    }

    /**
     * Adds the given matrix from this matrix, stores the result in a new matrix and returns that matrix
     *
     * @param m The matrix to subtract
     * @return A new matrix containing the result
     * @throws IllegalArgumentException If the given matrix is not the same size as this matrix
     */
    public BigMatrix subtract(BigMatrix m) {
        return copy().subtractAndSet(m);
    }

    /**
     * Multiplies this matrix with the given scalar, stores the result in a new matrix and returns that matrix
     *
     * @param scalar The scalar to multiply by
     * @return A new matrix containing the result
     */
    public BigMatrix multiply(BigFraction scalar) {
        return copy().multiplyAndSet(scalar);
    }

    /**
     * Computes {@code this * m}, stores the result in a new matrix and returns that matrix
     *
     * @param m The matrix to right-multiply by
     * @return A new matrix containing the result
     * @throws IllegalArgumentException If the number of columns in this matrix is not equal to the number of rows in
     *                                  the given matrix
     */
    public BigMatrix multiply(BigMatrix m) {
        if (this.columnCount != m.rowCount) {
            throw new IllegalArgumentException("Multiplying two matrices with disallowed dimensions");
        }

        BigMatrix dest = new BigMatrix(this.rowCount, m.columnCount);

        for (int row = 0; row < dest.rowCount; row++) {
            for (int column = 0; column < dest.columnCount; column++) {
                dest.set(row, column, this.getRow(row).dot(m.getColumn(column)));
            }
        }

        return dest;
    }

    /**
     * Computes {@code this * v}, treating the vector as a column vector, stores the result in a new vector and returns
     * that vector
     *
     * @param v The vector to right-multiply by
     * @return A new vector containing the result
     * @throws IllegalArgumentException If the dimension of the given vector is not equal to the number of columns in
     *                                  this matrix
     */
    public BigVector multiply(BigVector v) {
        if (this.columnCount != v.getDimension()) {
            throw new IllegalArgumentException("Vector length should equal the number of matrix columns");
        }

        BigVector dest = new BigVector(this.rowCount);

        for (int i = 0; i < this.rowCount; i++) {
            dest.set(i, v.dot(this.getRow(i)));
        }

        return dest;
    }

    /**
     * Divides this matrix with the given scalar, stores the result in a new matrix and returns that matrix
     *
     * @param scalar The scalar to divide by
     * @return A new matrix containing the result
     * @throws ArithmeticException If the given scalar is zero
     */
    public BigMatrix divide(BigFraction scalar) {
        return multiply(scalar.reciprocal());
    }

    /**
     * Swaps the two rows at the given indices, stores the result in a new matrix and returns that matrix
     *
     * @param row1 The row to swap with {@code row2}
     * @param row2 The row to swap with {@code row1}
     * @return A new matrix containing the result
     * @throws IndexOutOfBoundsException If {@code row1} or {@code row2} is out of bounds
     */
    public BigMatrix swapRows(int row1, int row2) {
        return copy().swapRowsAndSet(row1, row2);
    }

    /**
     * Swaps the two elements at the given indices, stores the result in a new matrix and returns that matrix
     *
     * @param row1 The row to swap with {@code row2}
     * @param col1 The col to swap with {@code col2}
     * @param row2 The row to swap with {@code row1}
     * @param col2 The col to swap with {@code col1}
     * @return A new matrix containing the result
     * @throws IndexOutOfBoundsException If {@code row1}, {@code col1}, {@code row2} or {@code col2} is out of bounds
     */
    public BigMatrix swapElements(int row1, int col1, int row2, int col2) {
        return copy().swapElementsAndSet(row1, col1, row2, col2);
    }

    /**
     * Computes the transpose of this matrix, stores the result in a new matrix and returns that matrix
     *
     * @return A new matrix containing the result
     */
    public BigMatrix transpose() {
        BigMatrix dest = new BigMatrix(this.getColumnCount(), this.getRowCount());

        for (int i = 0; i < this.columnCount; i++) {
            dest.setRow(i, this.getColumn(i));
        }

        return dest;
    }

    /**
     * Adds the given matrix to this matrix, modifying this matrix
     *
     * @param m The matrix to add to this matrix
     * @return This matrix
     * @throws IllegalArgumentException If the given matrix is not the same size as this matrix
     */
    public BigMatrix addAndSet(BigMatrix m) {
        if (this.getRowCount() != m.getRowCount() || this.getColumnCount() != m.getColumnCount()) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        if (this.columnCount == this.underlyingColumnCount && m.columnCount == m.underlyingColumnCount) {
            int size = rowCount * columnCount;
            for (int i = 0; i < size; i++) {
                numbers[startIndex + i] = numbers[startIndex + i].add(m.numbers[m.startIndex + i]);
            }
        } else {
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    set(row, col, get(row, col).add(m.get(row, col)));
                }
            }
        }

        return this;
    }

    /**
     * Subtracts the given matrix from this matrix, modifying this matrix
     *
     * @param m The matrix to subtract from this matrix
     * @return This matrix
     * @throws IllegalArgumentException If the given matrix is not the same size as this matrix
     */
    public BigMatrix subtractAndSet(BigMatrix m) {
        if (this.getRowCount() != m.getRowCount() || this.getColumnCount() != m.getColumnCount()) {
            throw new IllegalArgumentException("Subtracting two matrices with different dimensions");
        }

        if (this.columnCount == this.underlyingColumnCount && m.columnCount == m.underlyingColumnCount) {
            int size = rowCount * columnCount;
            for (int i = 0; i < size; i++) {
                numbers[startIndex + i] = numbers[startIndex + i].subtract(m.numbers[m.startIndex + i]);
            }
        } else {
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    set(row, col, get(row, col).subtract(m.get(row, col)));
                }
            }
        }

        return this;
    }

    /**
     * Multiplies this matrix by the given scalar, modifying this matrix
     *
     * @param scalar The scalar to multiply this matrix by
     * @return This matrix
     */
    public BigMatrix multiplyAndSet(BigFraction scalar) {
        if (this.columnCount == this.underlyingColumnCount) {
            int size = rowCount * columnCount;
            for (int i = 0; i < size; i++) {
                numbers[startIndex + i] = numbers[startIndex + i].multiply(scalar);
            }
        } else {
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    set(row, col, get(row, col).multiply(scalar));
                }
            }
        }
        return this;
    }

    /**
     * Computes {@code this * m} and stores the result in this matrix, modifying this matrix. This operation can only be
     * performed on square matrices.
     *
     * @param m The matrix to right-multiply this matrix by
     * @return This matrix
     * @throws IllegalArgumentException If this matrix is not a square matrix, or the given matrix is not the same size
     *                                  as this matrix
     */
    public BigMatrix multiplyAndSet(BigMatrix m) {
        // We have to modify this matrix, which means its dimensions must stay the same, which means it has to be square, and the same size as the other matrix
        if (this.rowCount != this.columnCount || m.rowCount != m.columnCount || this.rowCount != m.columnCount) {
            throw new IllegalArgumentException("Multiplying two matrices with disallowed dimensions");
        }

        BigMatrix result = this.multiply(m);

        for (int i = 0; i < this.getRowCount(); i++) {
            this.setRow(i, result.getRow(i));
        }

        return this;
    }

    /**
     * Divides this matrix by the given scalar, modifying this matrix
     *
     * @param scalar The scalar to divide this matrix by
     * @return This matrix
     * @throws ArithmeticException If the given scalar is zero
     */
    public BigMatrix divideAndSet(BigFraction scalar) {
        return multiplyAndSet(scalar.reciprocal());
    }

    /**
     * Swaps the two rows at the given indices, modifying this matrix
     *
     * @param row1 The row to swap with {@code row2}
     * @param row2 The row to swap with {@code row1}
     * @return This matrix
     * @throws IndexOutOfBoundsException If {@code row1} or {@code row2} is out of bounds
     */
    public BigMatrix swapRowsAndSet(int row1, int row2) {
        BigVector temp = this.getRow(row1).copy();
        this.setRow(row1, this.getRow(row2));
        this.setRow(row2, temp);
        return this;
    }

    /**
     * Swaps the two elements at the given indices, modifying this matrix
     *
     * @param row1 The row to swap with {@code row2}
     * @param col1 The col to swap with {@code col2}
     * @param row2 The row to swap with {@code row1}
     * @param col2 The col to swap with {@code col1}
     * @return This matrix
     * @throws IndexOutOfBoundsException If {@code row1}, {@code col1}, {@code row2} or {@code col2} is out of bounds
     */
    public BigMatrix swapElementsAndSet(int row1, int col1, int row2, int col2) {
        BigFraction temp = this.get(row1, col1);
        this.set(row1, col1, this.get(row2, col2));
        this.set(row2, col2, temp);
        return this;
    }

    /**
     * Place the row at endIndex before startIndex and shifts all the rows in between
     *
     * @param startIndex The starting index to swap
     * @param endIndex   The ending index to swap
     * @return This matrix
     * @throws IllegalArgumentException If {@code startIndex} is greater than {@code endIndex}
     */
    public BigMatrix shiftRows(int startIndex, int endIndex) {
        if (endIndex < startIndex) {
            throw new IllegalArgumentException("The ending index should be greater or equals to the starting one");
        }
        if (startIndex == endIndex) {return this;}
        for (int col = 0; col < this.getColumnCount(); col++) {
            BigFraction last = this.get(endIndex, col);
            for (int row = endIndex; row > startIndex; row--) {
                this.set(row, col, this.get(row - 1, col));
            }
            this.set(startIndex, col, last);
        }
        return this;
    }

    /**
     * Creates a copy of this matrix
     *
     * @return A copy of this matrix
     */
    public BigMatrix copy() {
        BigMatrix dest;
        if (columnCount == underlyingColumnCount) {
            dest = new BigMatrix(this.rowCount, this.columnCount);
            System.arraycopy(numbers, startIndex, dest.numbers, 0, dest.numbers.length);
        } else {
            dest = new BigMatrix(rowCount, columnCount, this::get);
        }
        return dest;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                h = 31 * h + get(row, col).hashCode();
            }
        }
        h = 31 * h + columnCount;
        h = 31 * h + rowCount;
        return h;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null || other.getClass() != BigMatrix.class) return false;
        BigMatrix that = (BigMatrix) other;
        if (this.rowCount != that.rowCount || this.columnCount != that.columnCount) {
            return false;
        }
        if (columnCount == underlyingColumnCount && that.columnCount == that.underlyingColumnCount) {
            int size = rowCount * columnCount;
            for (int i = 0; i < size; i++) {
                if (!that.numbers[that.startIndex + i].equals(this.numbers[this.startIndex + i])) {
                    return false;
                }
            }
        } else {
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    if (!that.get(row, col).equals(this.get(row, col))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        for (int i = 0; i < this.getRowCount(); i++) {
            sb.append(this.getRow(i)).append(i == this.getRowCount() - 1 ? "" : ", ");
        }

        return sb.append("}").toString();
    }

    /**
     * Constructs the identity matrix of the given size
     *
     * @param size The width and height of the identity matrix to construct
     * @return The identity matrix of the given size
     * @throws IllegalArgumentException If {@code size} isn't positive
     */
    public static BigMatrix identityMatrix(int size) {
        BigMatrix m = new BigMatrix(size, size);

        for (int i = 0; i < size; i++) {
            m.set(i, i, BigFraction.ONE);
        }

        return m;
    }

    /**
     * A function that returns the value that should go in a cell in a matrix based on the row and column
     */
    @FunctionalInterface
    public interface DataProvider {

        BigFraction getValue(int row, int col);

    }
}
