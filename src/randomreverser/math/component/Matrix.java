package randomreverser.math.component;

import randomreverser.util.MatrixDataProvider;
import randomreverser.util.StringUtils;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.DoubleSupplier;

public final class Matrix {

	protected Vector[] rows;
	protected int rowCount;
	protected int columnCount;

	public Matrix(int rowCount, int columnCount) {
		this.rowCount = rowCount;
		this.columnCount = columnCount;

		if(this.rowCount <= 0 || this.columnCount <= 0) {
			throw new IllegalArgumentException("Matrix dimensions cannot be less or equal to 0");
		}

		this.rows = new Vector[this.rowCount];
	}

	public Matrix(int rowCount, int columnCount, MatrixDataProvider gen) {
		this(rowCount, columnCount);

		for(int row = 0; row < this.rowCount; row++) {
			for(int col = 0; col < this.columnCount; col++) {
				this.set(row, col, gen.getValue(row, col));
			}
		}
	}

	public int getRowCount() {
		return this.rowCount;
	}

	public int getColumnCount() {
		return this.columnCount;
	}

	public double get(int row, int col) {
		if(this.rows[row] == null) {
			return Double.NaN;
		}

		return this.rows[row].get(col);
	}

	public void set(int row, int col, double value) {
		if(this.rows[row] == null) {
			this.rows[row] = new Vector(this.getColumnCount());
		}

		this.rows[row].set(col, value);
	}

	public Vector getRow(int i) {
		return this.rows[i];
	}

	public Vector getColumn(int i) {
		Vector col = new Vector(this.getRowCount());
		for (int j = 0; j < this.getRowCount(); j++) {
			col.set(j, this.get(j,i));
		}
		return col;
	}

	public void setRow(int i, Vector value) {
		if(value != null && value.getDimension() != this.getColumnCount()) {
			throw new IllegalArgumentException("Invalid vector length, expected " + this.getColumnCount() + ", got " + value.getDimension());
		}

		this.rows[i] = value;
	}

	public boolean isSquare() {
		return this.getRowCount() == this.getColumnCount();
	}

	public Matrix add(Matrix m) {
		if(this.getRowCount() != m.getRowCount() || this.getColumnCount() != m.getColumnCount()) {
			throw new IllegalArgumentException("Adding two matrices with different dimensions");
		}

		Matrix p = new Matrix(this.getRowCount(), m.getColumnCount());

		for(int i = 0; i < this.getRowCount(); i++) {
			p.setRow(i, this.getRow(i).add(m.getRow(i)));
		}

		return p;
	}

	public Matrix subtract(Matrix m) {
		if(this.getRowCount() != m.getRowCount() || this.getColumnCount() != m.getColumnCount()) {
			throw new IllegalArgumentException("Subtracting two matrices with different dimensions");
		}

		Matrix p = new Matrix(this.getRowCount(), m.getColumnCount());

		for(int i = 0; i < this.getRowCount(); i++) {
			p.setRow(i, this.getRow(i).subtract(m.getRow(i)));
		}

		return p;
	}

	public Matrix multiply(double scalar) {
		Matrix p = new Matrix(getRowCount(), getColumnCount());
		for (int i = 0; i < getRowCount(); i++) {
			for (int j = 0; j < getColumnCount(); j++) {
				p.set(i, j, get(i, j) * scalar);
			}
		}
		return p;
	}

	public Matrix multiply(Matrix m) {
		if(this.getColumnCount() != m.getRowCount()) {
			throw new IllegalArgumentException("Multiplying two matrices with disallowed dimensions");
		}

		Matrix p = new Matrix(this.getRowCount(), m.getColumnCount());

		for(int i = 0; i < p.getRowCount(); i++) {
			for(int j = 0; j < p.getColumnCount(); j++) {
				p.set(i, j, 0);

				for(int k = 0; k < m.getRowCount(); k++) {
					p.set(i, j, p.get(i, j) + this.get(i, k) * m.get(k, j));
				}
			}
		}

		return p;
	}

	public Vector multiply(Vector v) {
		if(this.getColumnCount() != v.getDimension()) {
			throw new IllegalArgumentException("Vector length should equal the number of matrix columns");
		}

		Vector r = new Vector(this.getRowCount());

		for(int i = 0; i < this.getRowCount(); i++) {
			r.set(i, v.dot(this.getRow(i)));
		}

		return r;
	}

	public Matrix divide(double scalar) {
		return multiply(1 / scalar);
	}

	public Matrix inverse() {
		if(this.getRowCount() != this.getColumnCount()) {
			throw new UnsupportedOperationException("Can only find the inverse of square matrices");
		}

		SystemSolver.Result result = SystemSolver.solve(this, identityMatrix(this.getRowCount()), SystemSolver.Phase.BASIS);

		if(result.type != SystemSolver.Result.Type.ONE_SOLUTION) {
			throw new IllegalStateException("This matrix is not invertible");
		}

		return result.result;
	}

	public Matrix swapRows(int i, int j) {
		Matrix m = this.copy();
		m.swapRowsEquals(i, j);
		return m;
	}

	public Matrix transpose() {
		Matrix p = new Matrix(this.getColumnCount(), this.getRowCount());

		for(int i = 0; i < this.getColumnCount(); i++) {
			p.setRow(i, this.getColumn(i).copy());
		}

		return p;
	}

	public Matrix addEquals(Matrix m) {
		if(this.getRowCount() != m.getRowCount() || this.getColumnCount() != m.getColumnCount()) {
			throw new IllegalArgumentException("Adding two matrices with different dimensions");
		}

		for(int i = 0; i < this.getRowCount(); i++) {
			this.getRow(i).addEquals(m.getRow(i));
		}

		return this;
	}

	public Matrix subtractEquals(Matrix m) {
		if(this.getRowCount() != m.getRowCount() || this.getColumnCount() != m.getColumnCount()) {
			throw new IllegalArgumentException("Subtracting two matrices with different dimensions");
		}

		for(int i = 0; i < this.getRowCount(); i++) {
			this.getRow(i).subtractEquals(m.getRow(i));
		}

		return this;
	}

	public Matrix multiplyEquals(Matrix m) {
		// We have to modify this matrix, which means its dimensions must stay the same, which means it has to be square, and the same size as the other matrix
		if(this.getRowCount() != this.getColumnCount() || m.getRowCount() != m.getColumnCount() || this.getRowCount() != m.getColumnCount()) {
			throw new IllegalArgumentException("Multiplying two matrices with disallowed dimensions");
		}

		Matrix result = this.multiply(m);

		for(int i = 0; i < this.getRowCount(); i++) {
			this.setRow(i, result.getRow(i));
		}

		return this;
	}

	public Matrix swapRowsEquals(int i, int j) {
		Vector temp = this.getRow(i);
		this.setRow(i, this.getRow(j));
		this.setRow(j, temp);
		return this;
	}

	public Matrix copy() {
		Matrix m = new Matrix(this.getRowCount(), this.getColumnCount());

		for(int i = 0; i < m.getRowCount(); i++) {
			m.setRow(i, this.getRow(i) == null ? null : this.getRow(i).copy());
		}

		return m;
	}

	public String toPrettyString() {
		return StringUtils.tableToString(rowCount, columnCount, (row, column) -> String.valueOf(get(row, column)));
	}

	public boolean equals(Matrix other, double tolerance) {
		if (this.rowCount != other.rowCount || this.columnCount != other.columnCount) {
			return false;
		}
		for (int i = 0; i < rows.length; i++) {
			if (!rows[i].equals(other.rows[i], tolerance)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int h = Arrays.hashCode(rows);
		h = 31 * h + columnCount;
		h = 31 * h + rowCount;
		return h;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) return true;
		if (other == null || other.getClass() != Matrix.class) return false;
		Matrix that = (Matrix) other;
		return this.columnCount == that.columnCount && this.rowCount == that.rowCount && Arrays.equals(this.rows, that.rows);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");

		for(int i = 0; i < this.getRowCount(); i++) {
			sb.append(this.getRow(i)).append(i == this.getRowCount() - 1 ? "" : ", ");
		}

		return sb.append("}").toString();
	}

	public static Matrix fromString(String raw) {
		Matrix m = null;
		int height;
		int width;

		raw = raw.replaceAll("\\s+","");

		if (!raw.startsWith("{") || !raw.endsWith("}")) {
			throw new IllegalArgumentException("Malformed matrix");
		}

		raw = raw.substring(2, raw.length() - 2);
		String[] data = raw.split("},\\{");
		height = data.length;

		for(int i = 0; i < height; i++) {
			Vector v = Vector.fromString(data[i]);

			if(i == 0) {
				width = v.getDimension();
				m = new Matrix(height,width);
			}

			m.setRow(i, v);
		}

		return m;
	}

	public static Matrix fromBigMatrix(BigMatrix m) {
		Matrix p = new Matrix(m.getRowCount(), m.getColumnCount());

		for(int i = 0; i < p.getRowCount(); i++) {
			p.setRow(i, Vector.fromBigVector(m.getRow(i)));
		}

		return p;
	}

	public static Matrix identityMatrix(int size) {
		Matrix m = new Matrix(size, size);

		for(int i = 0; i < size; i++) {
			m.set(i, i, 1.0D);
		}

		return m;
	}

}
