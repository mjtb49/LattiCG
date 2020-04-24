package randomreverser.math.component;

import randomreverser.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.regex.Pattern;

public final class BigMatrix {

	protected BigVector[] rows;
	protected int rowCount;
	protected int columnCount;

	public BigMatrix(int rowCount, int columnCount) {
		this.rowCount = rowCount;
		this.columnCount = columnCount;

		if(this.rowCount <= 0 || this.columnCount <= 0) {
			throw new IllegalArgumentException("Matrix dimensions cannot be less or equal to 0");
		}

		this.rows = new BigVector[this.rowCount];
	}

	public int getRowCount() {
		return this.rowCount;
	}

	public int getColumnCount() {
		return this.columnCount;
	}

	public BigDecimal get(int row, int col) {
		if(this.rows[row] == null) {
			return null;
		}

		return this.rows[row].get(col);
	}

	public void set(int i, int j, BigDecimal value) {
		if(this.rows[i] == null) {
			this.rows[i] = new BigVector(this.getColumnCount());
		}

		this.rows[i].set(j, value);
	}

	public BigVector getRow(int i) {
		return this.rows[i];
	}

	public void setRow(int i, BigVector value) {
		if(value != null && value.getDimension() != this.getColumnCount()) {
			throw new IllegalArgumentException("Invalid vector length, expected " + this.getColumnCount() + ", got " + value.getDimension());
		}

		this.rows[i] = value;
	}

	public BigMatrix add(BigMatrix m) {
		if(this.getRowCount() != m.getRowCount() || this.getColumnCount() != m.getColumnCount()) {
			throw new IllegalArgumentException("Adding two matrices with different dimensions");
		}

		BigMatrix p = new BigMatrix(this.getRowCount(), m.getColumnCount());

		for(int i = 0; i < this.getRowCount(); i++) {
			p.setRow(i, this.getRow(i).add(m.getRow(i)));
		}

		return p;
	}

	public BigMatrix subtract(BigMatrix m) {
		if(this.getRowCount() != m.getRowCount() || this.getColumnCount() != m.getColumnCount()) {
			throw new IllegalArgumentException("Subtracting two matrices with different dimensions");
		}

		BigMatrix p = new BigMatrix(this.getRowCount(), m.getColumnCount());

		for(int i = 0; i < this.getRowCount(); i++) {
			p.setRow(i, this.getRow(i).subtract(m.getRow(i)));
		}

		return p;
	}

	public BigMatrix multiply(BigDecimal scalar) {
		BigMatrix p = new BigMatrix(getRowCount(), getColumnCount());
		for (int i = 0; i < getRowCount(); i++) {
			for (int j = 0; j < getColumnCount(); j++) {
				p.set(i, j, get(i, j).multiply(scalar));
			}
		}
		return p;
	}

	public BigMatrix multiply(BigMatrix m) {
		if(this.getColumnCount() != m.getRowCount()) {
			throw new IllegalArgumentException("Multiplying two matrices with disallowed dimensions");
		}

		BigMatrix p = new BigMatrix(this.getRowCount(), m.getColumnCount());

		for(int i = 0; i < p.getRowCount(); i++) {
			for(int j = 0; j < p.getColumnCount(); j++) {
				p.set(i, j, BigDecimal.ZERO);

				for(int k = 0; k < m.getRowCount(); k++) {
					p.set(i, j, p.get(i, j).add(this.get(i, k).multiply(m.get(k, j))));
				}
			}
		}

		return p;
	}

	public BigMatrix divide(BigDecimal scalar) {
		return multiply(BigDecimal.ONE.divide(scalar));
	}

	public BigMatrix inverse() {
		if(this.getRowCount() != this.getColumnCount()) {
			throw new UnsupportedOperationException("Can only find the inverse of square matrices");
		}
		if (this.getRowCount() == 1) {
			BigMatrix r = new BigMatrix(1,1);
			r.set(0,0,BigDecimal.ONE.divide(this.get(0,0), RoundingMode.HALF_UP));
			return r;
		}
		SystemSolver.BigResult result = SystemSolver.solve(this, BigMatrix.identityMatrix(this.getRowCount()), SystemSolver.Phase.BASIS);

		if(result.type != SystemSolver.BigResult.Type.ONE_SOLUTION) {
			throw new IllegalStateException("This matrix is not invertible");
		}

		return result.result;
	}

	public BigMatrix swapRows(int i, int j) {
		BigMatrix m = this.copy();
		m.swapRowsEquals(i, j);
		return m;
	}

	public BigMatrix transpose() {
		BigMatrix p = new BigMatrix(this.getColumnCount(), this.getRowCount());

		for (int i = 0; i < getRowCount(); i++) {
			for (int j = 0; j < getColumnCount(); j++) {
				p.set(j, i, get(i, j));
			}
		}

		return p;
	}

	public BigMatrix addEquals(BigMatrix m) {
		if(this.getRowCount() != m.getRowCount() || this.getColumnCount() != m.getColumnCount()) {
			throw new IllegalArgumentException("Adding two matrices with different dimensions");
		}

		for(int i = 0; i < this.getRowCount(); i++) {
			this.getRow(i).addEquals(m.getRow(i));
		}

		return this;
	}

	public BigMatrix subtractEquals(BigMatrix m) {
		if(this.getRowCount() != m.getRowCount() || this.getColumnCount() != m.getColumnCount()) {
			throw new IllegalArgumentException("Subtracting two matrices with different dimensions");
		}

		for(int i = 0; i < this.getRowCount(); i++) {
			this.getRow(i).subtractEquals(m.getRow(i));
		}

		return this;
	}

	public BigMatrix multiplyEquals(BigMatrix m) {
		if(this.getRowCount() != m.getRowCount() || this.getColumnCount() != m.getColumnCount()) {
			throw new IllegalArgumentException("Multiplying two matrices with disallowed dimensions");
		}

		BigMatrix result = this.multiply(m);

		for(int i = 0; i < this.getRowCount(); i++) {
			this.setRow(i, result.getRow(i));
		}

		return this;
	}

	public BigMatrix swapRowsEquals(int i, int j) {
		BigVector temp = this.getRow(i);
		this.setRow(i, this.getRow(j));
		this.setRow(j, temp);
		return this;
	}

	public BigMatrix copy() {
		BigMatrix m = new BigMatrix(this.getRowCount(), this.getColumnCount());

		for(int i = 0; i < m.getRowCount(); i++) {
			m.setRow(i, this.getRow(i) == null ? null : this.getRow(i).copy());
		}

		return m;
	}

	public String toPrettyString() {
		return StringUtils.tableToString(getRowCount(), getColumnCount(), (row, column) -> get(row, column) == null ? "null" : get(row, column).stripTrailingZeros().toPlainString());
	}

	public boolean equals(BigMatrix other, BigDecimal tolerance) {
		if (this.rowCount != other.rowCount || this.columnCount != other.columnCount) {
			return false;
		}
		for (int i = 0; i < rowCount; i++) {
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
		if (other == null || other.getClass() != BigMatrix.class) return false;
		BigMatrix that = (BigMatrix) other;
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

	public static BigMatrix fromString(String raw) {
		BigMatrix m = null;
		int height;
		int width;

		raw = raw.replaceAll("\\s+","");

		if(!raw.startsWith("{") || !raw.endsWith("}")) {
			throw new IllegalArgumentException("Malformed matrix");
		}

		raw = raw.substring(2, raw.length() - 2);
		String[] data = raw.split(Pattern.quote("},{"));
		height = data.length;

		for(int i = 0; i < height; i++) {
			BigVector v = BigVector.fromString(data[i]);

			if(i == 0) {
				width = v.getDimension();
				m = new BigMatrix(height,width);
			}

			m.setRow(i, v);
		}

		return m;
	}

	public static BigMatrix fromMatrix(Matrix m) {
		BigMatrix p = new BigMatrix(m.getRowCount(), m.getColumnCount());

		for(int i = 0; i < p.getRowCount(); i++) {
			p.setRow(i, BigVector.fromVector(m.getRow(i)));
		}

		return p;
	}

	public static BigMatrix identityMatrix(int size) {
		BigMatrix m = new BigMatrix(size, size);
		for (int i = 0; i < size; i++) for (int j = 0; j < size; j++)
			m.set(i, j, BigDecimal.ZERO);
		for(int i = 0; i < size; i++) {
			m.set(i, i, BigDecimal.ONE);
		}

		return m;
	}
}
