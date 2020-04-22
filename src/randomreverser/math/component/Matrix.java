package randomreverser.math.component;

import randomreverser.util.StringUtils;

import java.security.InvalidParameterException;
import java.util.regex.Pattern;

public class Matrix {

	protected Vector[] rows;
	protected int height;
	protected int width;

	public Matrix(int height, int width) {
		this.height = height;
		this.width = width;

		if(this.height <= 0 || this.width <= 0) {
			throw new InvalidParameterException("Matrix dimensions cannot be less or equal to 0");
		}

		this.rows = new Vector[this.height];
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public double get(int row, int col) {
		if(this.rows[row] == null) {
			return Double.NaN;
		}

		return this.rows[row].get(col);
	}

	public void set(int i, int j, double value) {
		if(this.rows[i] == null) {
			this.rows[i] = new Vector(this.getWidth());
		}

		this.rows[i].set(j, value);
	}

	public Vector getRow(int i) {
		return this.rows[i];
	}

	public Vector getColumn(int i) {
		Vector col = new Vector(this.getHeight());
		for (int j = 0; j < this.getHeight(); j++) {
			col.set(j, this.get(j,i));
		}
		return col;
	}

	public void setRow(int i, Vector value) {
		if(value != null && value.getLength() != this.getWidth()) {
			throw new InvalidParameterException("Invalid vector length, expected " + this.getWidth() + ", got " + value.getLength());
		}

		this.rows[i] = value;
	}

	public Matrix add(Matrix m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			throw new UnsupportedOperationException("Adding two matrices with different dimensions");
		}

		Matrix p = new Matrix(this.getHeight(), m.getWidth());

		for(int i = 0; i < this.getHeight(); i++) {
			p.setRow(i, this.getRow(i).add(m.getRow(i)));
		}

		return p;
	}

	public Matrix subtract(Matrix m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			throw new UnsupportedOperationException("Subtracting two matrices with different dimensions");
		}

		Matrix p = new Matrix(this.getHeight(), m.getWidth());

		for(int i = 0; i < this.getHeight(); i++) {
			p.setRow(i, this.getRow(i).subtract(m.getRow(i)));
		}

		return p;
	}

	public Matrix multiply(Matrix m) {
		if(this.getWidth() != m.getHeight()) {
			throw new UnsupportedOperationException("Multiplying two matrices with disallowed dimensions");
		}

		Matrix p = new Matrix(this.getHeight(), m.getWidth());

		for(int i = 0; i < p.getHeight(); i++) {
			for(int j = 0; j < p.getWidth(); j++) {
				p.set(i, j, 0);

				for(int k = 0; k < m.getHeight(); k++) {
					p.set(i, j, p.get(i, j) + this.get(i, k) * m.get(k, j));
				}
			}
		}

		return p;
	}

	public Vector multiply(Vector v) {
		if(this.getWidth() != v.getLength()) {
			throw new UnsupportedOperationException("Vector length should equal the matrix width");
		}

		Vector r = new Vector(this.getHeight());

		for(int i = 0; i < this.getHeight(); i++) {
			r.set(i, v.dot(this.getRow(i)));
		}

		return r;
	}

	public Matrix inverse() {
		if(this.getHeight() != this.getWidth()) {
			throw new UnsupportedOperationException("Can only find the inverse of square matrices");
		}

		SystemSolver.Result result = SystemSolver.solve(this, new Factory().identityMatrix(this.getHeight()), SystemSolver.Phase.BASIS);

		if(result.type != SystemSolver.Result.Type.ONE_SOLUTION) {
			throw new UnsupportedOperationException("This matrix is not invertible");
		}

		return result.result;
	}

	public Matrix swap(int i, int j) {
		Matrix m = this.copy();
		m.swapEquals(i, j);
		return m;
	}

	public Matrix transpose() {
		Matrix p = new Matrix(this.getHeight(), this.getWidth());

		for(int i = 0; i < this.getHeight(); i++) {
			p.setRow(i, this.getColumn(i).copy());
		}

		return p;
	}

	public void addEquals(Matrix m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			throw new UnsupportedOperationException("Adding two matrices with different dimensions");
		}

		for(int i = 0; i < this.getHeight(); i++) {
			this.getRow(i).addEquals(m.getRow(i));
		}
	}

	public void subtractEquals(Matrix m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			throw new UnsupportedOperationException("Subtracting two matrices with different dimensions");
		}

		for(int i = 0; i < this.getHeight(); i++) {
			this.getRow(i).subtractEquals(m.getRow(i));
		}
	}

	public void multiplyEquals(Matrix m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			throw new UnsupportedOperationException("Multiplying two matrices with disallowed dimensions");
		}

		Matrix result = this.multiply(m);

		for(int i = 0; i < this.getHeight(); i++) {
			this.setRow(i, result.getRow(i));
		}
	}

	public void swapEquals(int i, int j) {
		Vector temp = this.getRow(i);
		this.setRow(i, this.getRow(j));
		this.setRow(j, temp);
	}

	public Matrix copy() {
		Matrix m = new Matrix(this.getHeight(), this.getWidth());

		for(int i = 0; i < m.getHeight(); i++) {
			m.setRow(i, this.getRow(i) == null ? null : this.getRow(i).copy());
		}

		return m;
	}

	public String toPrettyString() {
		return StringUtils.tableToString(height, width, (row, column) -> String.valueOf(get(row, column)));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");

		for(int i = 0; i < this.getHeight(); i++) {
			sb.append(this.getRow(i)).append(i == this.getHeight() - 1 ? "" : ", ");
		}

		return sb.append("}").toString();
	}

	public static class Builder {
		private int height;
		private int width;
		private double defaultValue;

		public Builder setSize(int height, int width) {
			this.height = height;
			this.width = width;
			return this;
		}

		public Builder fillWith(double defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public Matrix build() {
			Matrix m = new Matrix(this.height, this.width);

			if(this.defaultValue != 0.0D) {
				for(int i = 0; i < m.getHeight(); i++) {
					m.setRow(i, new Vector.Builder().setLength(m.getWidth()).fillWith(this.defaultValue).build());
				}
			}

			return m;
		}
	}

	public static class Factory {
		public Matrix fromString(String raw) {
			Matrix m = null;
			int height = 0;
			int width = 0;

			raw = raw.replaceAll("\\s+","");

			if(!raw.startsWith("{") || !raw.endsWith("}")) {
				throw new InvalidParameterException("Malformated query");
			}

			raw = raw.substring(2, raw.length() - 2);
			String[] data = raw.split(Pattern.quote("},{"));
			height = data.length;

			for(int i = 0; i < height; i++) {
				Vector v = new Vector.Factory().fromString(data[i]);

				if(i == 0) {
					width = v.getLength();
					m = new Matrix(height,width);
				}

				m.setRow(i, v);
			}

			return m;
		}

		public Matrix fromBigMatrix(BigMatrix m) {
			Matrix p = new Matrix(m.getHeight(), m.getWidth());

			for(int i = 0; i < p.getHeight(); i++) {
				p.setRow(i, new Vector.Factory().fromBigVector(m.getRow(i)));
			}

			return p;
		}

		public Matrix identityMatrix(int size) {
			Matrix m = new Matrix(size, size);

			for(int i = 0; i < size; i++) {
				m.set(i, i, 1.0D);
			}

			return m;
		}
	}

}
