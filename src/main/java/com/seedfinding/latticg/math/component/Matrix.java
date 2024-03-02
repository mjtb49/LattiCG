package com.seedfinding.latticg.math.component;

import com.seedfinding.latticg.math.decomposition.LUDecomposition;
import com.seedfinding.latticg.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A matrix of double values
 */
public final class Matrix {

    private final double[] numbers;
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
    public Matrix(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.underlyingColumnCount = columnCount;

        if (rowCount <= 0 || columnCount <= 0) {
            throw new IllegalArgumentException("Matrix dimensions cannot be less or equal to 0");
        }

        this.numbers = new double[rowCount * columnCount];
    }

    /**
     * Constructs a matrix of the given size, using the given function to fill in each element.
     *
     * @param rowCount    The number of rows
     * @param columnCount The number of columns
     * @param gen         The function to call for each element of the matrix
     */
    public Matrix(int rowCount, int columnCount, DataProvider gen) {
        this(rowCount, columnCount);

        for (int row = 0; row < this.rowCount; row++) {
            for (int col = 0; col < this.columnCount; col++) {
                this.set(row, col, gen.getValue(row, col));
            }
        }
    }

    private Matrix(int rowCount, int columnCount, double[] numbers, int startIndex, int underlyingColumnCount) {
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
    public double get(int row, int col) {
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
    public void set(int row, int col, double value) {
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
    public Vector getRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rowCount) {
            throw new IndexOutOfBoundsException("Index " + rowIndex + ", size " + rowCount);
        }
        return Vector.createView(numbers, columnCount, startIndex + rowIndex * underlyingColumnCount, 1);
    }

    /**
     * Gets a vector <i>view</i> of the column of the given index. Modifying this vector will modify the original matrix
     *
     * @param columnIndex The index of the column to get
     * @return A view of the column at {@code columnIndex}
     * @throws IndexOutOfBoundsException If {@code columnIndex} is out of bounds
     */
    public Vector getColumn(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columnCount) {
            throw new IndexOutOfBoundsException("Index " + columnIndex + ", size " + columnCount);
        }
        return Vector.createView(numbers, rowCount, startIndex + columnIndex, underlyingColumnCount);
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
    public void setRow(int rowIndex, Vector newRow) {
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
    public void setColumn(int columnIndex, Vector newColumn) {
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
    public Matrix submatrix(int startRow, int startColumn, int rowCount, int columnCount) {
        if (startRow < 0 || startColumn < 0 || rowCount <= 0 || columnCount <= 0 || startRow + rowCount > this.rowCount || startColumn + columnCount > this.columnCount) {
            throw new IllegalArgumentException(String.format("Illegal submatrix start (%d, %d) with size (%d, %d), size of original matrix (%d, %d)", startRow, startColumn, rowCount, columnCount, this.rowCount, this.columnCount));
        }
        return new Matrix(rowCount, columnCount, numbers, startIndex + startColumn + underlyingColumnCount * startRow, underlyingColumnCount);
    }

    /**
     * Adds the given matrix to this matrix, stores the result in a new matrix and returns that matrix
     *
     * @param m The matrix to add
     * @return A new matrix containing the result
     * @throws IllegalArgumentException If the given matrix is not the same size as this matrix
     */
    public Matrix add(Matrix m) {
        return copy().addAndSet(m);
    }

    /**
     * Adds the given matrix from this matrix, stores the result in a new matrix and returns that matrix
     *
     * @param m The matrix to subtract
     * @return A new matrix containing the result
     * @throws IllegalArgumentException If the given matrix is not the same size as this matrix
     */
    public Matrix subtract(Matrix m) {
        return copy().subtractAndSet(m);
    }

    /**
     * Multiplies this matrix with the given scalar, stores the result in a new matrix and returns that matrix
     *
     * @param scalar The scalar to multiply by
     * @return A new matrix containing the result
     */
    public Matrix multiply(double scalar) {
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
    public Matrix multiply(Matrix m) {
        if (this.columnCount != m.rowCount) {
            throw new IllegalArgumentException("Multiplying two matrices with disallowed dimensions");
        }

        Matrix dest = new Matrix(this.rowCount, m.columnCount);

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
    public Vector multiply(Vector v) {
        if (this.columnCount != v.getDimension()) {
            throw new IllegalArgumentException("Vector length should equal the number of matrix columns");
        }

        Vector dest = new Vector(this.rowCount);

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
     */
    public Matrix divide(double scalar) {
        return copy().divideAndSet(scalar);
    }

    /**
     * Computes the inverse of this matrix, {@code this}<sup>-1</sup>, stores the result in a new matrix and returns
     * that matrix
     *
     * @return A new matrix containing the result
     * @throws UnsupportedOperationException If this is not a square matrix
     * @throws IllegalStateException         If this matrix is singular
     */
    public Matrix inverse() {
        return LUDecomposition.decompose(this).inverse();
    }

    /**
     * Swaps the two rows at the given indices, stores the result in a new matrix and returns that matrix
     *
     * @param row1 The row to swap with {@code row2}
     * @param row2 The row to swap with {@code row1}
     * @return A new matrix containing the result
     * @throws IndexOutOfBoundsException If {@code row1} or {@code row2} is out of bounds
     */
    public Matrix swapRows(int row1, int row2) {
        return copy().swapRowsAndSet(row1, row2);
    }

    /**
     * Computes the transpose of this matrix, stores the result in a new matrix and returns that matrix
     *
     * @return A new matrix containing the result
     */
    public Matrix transpose() {
        Matrix dest = new Matrix(this.columnCount, this.rowCount);

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
    public Matrix addAndSet(Matrix m) {
        if (this.rowCount != m.rowCount || this.columnCount != m.columnCount) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        if (this.columnCount == this.underlyingColumnCount && m.columnCount == m.underlyingColumnCount) {
            int size = rowCount * columnCount;
            for (int i = 0; i < size; i++) {
                numbers[startIndex + i] += m.numbers[m.startIndex + i];
            }
        } else {
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    set(row, col, get(row, col) + m.get(row, col));
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
    public Matrix subtractAndSet(Matrix m) {
        if (this.rowCount != m.rowCount || this.columnCount != m.columnCount) {
            throw new IllegalArgumentException("Subtracting two matrices with different dimensions");
        }

        if (this.columnCount == this.underlyingColumnCount && m.columnCount == m.underlyingColumnCount) {
            int size = rowCount * columnCount;
            for (int i = 0; i < size; i++) {
                numbers[startIndex + i] -= m.numbers[m.startIndex + i];
            }
        } else {
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    set(row, col, get(row, col) - m.get(row, col));
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
    public Matrix multiplyAndSet(double scalar) {
        if (this.columnCount == this.underlyingColumnCount) {
            int size = rowCount * columnCount;
            for (int i = 0; i < size; i++) {
                numbers[startIndex + i] *= scalar;
            }
        } else {
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    set(row, col, get(row, col) * scalar);
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
    public Matrix multiplyAndSet(Matrix m) {
        // We have to modify this matrix, which means its dimensions must stay the same, which means it has to be square, and the same size as the other matrix
        if (this.rowCount != this.columnCount || m.rowCount != m.columnCount || this.rowCount != m.columnCount) {
            throw new IllegalArgumentException("Multiplying two matrices with disallowed dimensions");
        }

        Matrix result = this.multiply(m);

        for (int i = 0; i < this.rowCount; i++) {
            this.setRow(i, result.getRow(i));
        }

        return this;
    }

    /**
     * Divides this matrix by the given scalar, modifying this matrix
     *
     * @param scalar The scalar to divide this matrix by
     * @return This matrix
     */
    public Matrix divideAndSet(double scalar) {
        return multiplyAndSet(1 / scalar);
    }

    /**
     * Swaps the two rows at the given indices, modifying this matrix
     *
     * @param row1 The row to swap with {@code row2}
     * @param row2 The row to swap with {@code row1}
     * @return This matrix
     * @throws IndexOutOfBoundsException If {@code row1} or {@code row2} is out of bounds
     */
    public Matrix swapRowsAndSet(int row1, int row2) {
        Vector temp = this.getRow(row1).copy();
        this.setRow(row1, this.getRow(row2));
        this.setRow(row2, temp);
        return this;
    }

    /**
     * Creates a copy of this matrix
     *
     * @return A copy of this matrix
     */
    public Matrix copy() {
        Matrix dest;
        if (columnCount == underlyingColumnCount) {
            dest = new Matrix(rowCount, columnCount);
            System.arraycopy(numbers, startIndex, dest.numbers, 0, dest.numbers.length);
        } else {
            dest = new Matrix(rowCount, columnCount, this::get);
        }
        return dest;
    }

    /**
     * Formats this matrix nicely into a human-readable multi-line string
     *
     * @return The formatted matrix
     */
    public String toPrettyString() {
        return StringUtils.tableToString(rowCount, columnCount, (row, column) -> String.valueOf(get(row, column)));
    }

    /**
     * Returns whether this matrix has the same dimensions as the given matrix, and all elements of this matrix are
     * within {@code tolerance} of the corresponding elements in the given matrix
     *
     * @param other     The matrix to test against
     * @param tolerance The maximum amount each element is allowed to differ
     * @return Whether this matrix is close enough to the given matrix
     */
    public boolean equals(Matrix other, double tolerance) {
        if (this.rowCount != other.rowCount || this.columnCount != other.columnCount) {
            return false;
        }
        if (columnCount == underlyingColumnCount && other.columnCount == other.underlyingColumnCount) {
            int size = rowCount * columnCount;
            for (int i = 0; i < size; i++) {
                if (Math.abs(other.numbers[other.startIndex + i] - this.numbers[this.startIndex + i]) > tolerance) {
                    return false;
                }
            }
        } else {
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    if (Math.abs(other.get(row, col) - this.get(row, col)) > tolerance) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                h = 31 * h + Double.hashCode(get(row, col));
            }
        }
        h = 31 * h + columnCount;
        h = 31 * h + rowCount;
        return h;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null || other.getClass() != Matrix.class) return false;
        Matrix that = (Matrix) other;
        return this.equals(that, 0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        for (int i = 0; i < this.rowCount; i++) {
            sb.append(this.getRow(i)).append(i == this.rowCount - 1 ? "" : ", ");
        }

        return sb.append("}").toString();
    }

    public static Matrix fromString(String str) {
        str = str.trim();
        if (!str.startsWith("{") || !str.endsWith("}")) {
            throw new IllegalArgumentException("Illegal Matrix format");
        }
        List<Vector> rows = new ArrayList<>();
        int vectorEnd;
        for (int vectorStart = str.indexOf('{', 1); vectorStart >= 0; vectorStart = str.indexOf('{', vectorEnd + 1)) {
            vectorEnd = str.indexOf('}', vectorStart + 1);
            rows.add(Vector.fromString(str.substring(vectorStart, vectorEnd + 1)));
        }

        if (rows.isEmpty()) {
            return new Matrix(0, 0);
        }

        Matrix matrix = new Matrix(rows.size(), rows.get(0).getDimension());
        for (int i = 0; i < rows.size(); i++) {
            matrix.setRow(i, rows.get(i));
        }
        return matrix;
    }

    /**
     * Converts a {@link BigMatrix} to a matrix of doubles
     *
     * @param m The {@code BigMatrix} to copy from
     * @return A matrix of doubles with the same values as the input matrix, where possible
     */
    public static Matrix fromBigMatrix(BigMatrix m) {
        Matrix p = new Matrix(m.getRowCount(), m.getColumnCount());

        for (int i = 0; i < p.rowCount; i++) {
            p.setRow(i, Vector.fromBigVector(m.getRow(i)));
        }

        return p;
    }

    /**
     * Constructs the identity matrix of the given size
     *
     * @param size The width and height of the identity matrix to construct
     * @return The identity matrix of the given size
     * @throws IllegalArgumentException If {@code size} isn't positive
     */
    public static Matrix identityMatrix(int size) {
        Matrix m = new Matrix(size, size);

        for (int i = 0; i < size; i++) {
            m.set(i, i, 1.0D);
        }

        return m;
    }

    /**
     * A function that returns the value that should go in a cell in a matrix based on the row and column
     */
    @FunctionalInterface
    public interface DataProvider {

        double getValue(int row, int col);

    }
}
