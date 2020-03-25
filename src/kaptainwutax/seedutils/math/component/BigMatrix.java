package kaptainwutax.seedutils.math.component;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.regex.Pattern;

public class BigMatrix implements ICopy<BigMatrix> {

	protected BigVector[] rows;
	protected int height;
	protected int width;

	public BigMatrix(int height, int width) {
		this.height = height;
		this.width = width;

		if(this.height <= 0 || this.width <= 0) {
			throw new InvalidParameterException("Matrix dimensions cannot be less or equal to 0");
		}

		this.rows = new BigVector[this.height];
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public BigDecimal get(int i, int j) {
		return this.rows[i].get(j);
	}

	public void set(int i, int j, BigDecimal value) {
		if(this.rows[i] == null) {
			this.rows[i] = new BigVector(this.getWidth());
		}

		this.rows[i].set(j, value);
	}

	public BigVector getRow(int i) {
		return this.rows[i];
	}

	public void setRow(int i, BigVector value) {
		if(value.getLength() != this.getWidth()) {
			throw new InvalidParameterException("Invalid vector length, expected " + this.getWidth() + ", got " + value.getLength());
		}

		this.rows[i] = value;
	}

	public BigMatrix add(BigMatrix m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			throw new UnsupportedOperationException("Adding two matrices with different dimensions");
		}

		BigMatrix p = new BigMatrix(this.getHeight(), m.getWidth());

		for(int i = 0; i < this.getHeight(); i++) {
			p.setRow(i, this.getRow(i).add(m.getRow(i)));
		}

		return p;
	}

	public BigMatrix subtract(BigMatrix m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			throw new UnsupportedOperationException("Subtracting two matrices with different dimensions");
		}

		BigMatrix p = new BigMatrix(this.getHeight(), m.getWidth());

		for(int i = 0; i < this.getHeight(); i++) {
			p.setRow(i, this.getRow(i).subtract(m.getRow(i)));
		}

		return p;
	}

	public BigMatrix multiply(BigMatrix m) {
		if(this.getWidth() != m.getHeight()) {
			throw new UnsupportedOperationException("Multiplying two matrices with disallowed dimensions");
		}

		BigMatrix p = new BigMatrix(this.getHeight(), m.getWidth());

		for(int i = 0; i < p.getHeight(); i++) {
			for(int j = 0; j < p.getWidth(); j++) {
				p.set(i, j, BigDecimal.ZERO);

				for(int k = 0; k < m.getHeight(); k++) {
					p.set(i, j, p.get(i, j).add(this.get(i, k).multiply(m.get(k, j))));
				}
			}
		}

		return p;
	}

	public BigMatrix swap(int i, int j) {
		BigMatrix m = this.copy();
		m.swapEquals(i, j);
		return m;
	}

	public BigMatrix transpose() {
		BigMatrix p = new BigMatrix(this.getWidth(), this.getHeight());

		for(int i = 0; i < this.getHeight(); i++) {
			p.setRow(i, this.getRow(i).copy());
		}

		return p;
	}

	public void addEquals(BigMatrix m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			throw new UnsupportedOperationException("Adding two matrices with different dimensions");
		}

		for(int i = 0; i < this.getHeight(); i++) {
			this.getRow(i).addEquals(m.getRow(i));
		}
	}

	public void subtractEquals(BigMatrix m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			throw new UnsupportedOperationException("Subtracting two matrices with different dimensions");
		}

		for(int i = 0; i < this.getHeight(); i++) {
			this.getRow(i).subtractEquals(m.getRow(i));
		}
	}

	public void multiplyEquals(BigMatrix m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			throw new UnsupportedOperationException("Multiplying two matrices with disallowed dimensions");
		}

		BigMatrix result = this.multiply(m);

		for(int i = 0; i < this.getHeight(); i++) {
			this.setRow(i, result.getRow(i));
		}
	}

	public void swapEquals(int i, int j) {
		BigVector temp = this.getRow(i);
		this.setRow(i, this.getRow(j));
		this.setRow(j, temp);
	}

	@Override
	public BigMatrix copy() {
		BigMatrix m = new BigMatrix(this.getHeight(), this.getWidth());

		for(int i = 0; i < m.getHeight(); i++) {
			m.setRow(i, this.getRow(i) == null ? null : this.getRow(i).copy());
		}

		return m;
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
		private BigDecimal defaultValue;

		public Builder setSize(int height, int width) {
			this.height = height;
			this.width = width;
			return this;
		}

		public Builder fillWith(BigDecimal defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public BigMatrix build() {
			BigMatrix m = new BigMatrix(this.height, this.width);

			if(this.defaultValue != null) {
				for(int i = 0; i < m.getHeight(); i++) {
					m.setRow(i, new BigVector.Builder().setLength(m.getWidth()).fillWith(this.defaultValue).build());
				}
			}

			return m;
		}
	}

	public static class Factory {
		public BigMatrix fromString(String raw) {
			BigMatrix m = null;
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
				BigVector v = new BigVector.Factory().fromString(data[i]);

				if(i == 0) {
					width = v.getLength();
					m = new BigMatrix(height,width);
				}

				m.setRow(i, v);
			}

			return m;
		}

		public BigMatrix fromMatrix(Matrix m) {
			BigMatrix p = new BigMatrix(m.getHeight(), m.getWidth());

			for(int i = 0; i < p.getHeight(); i++) {
				p.setRow(i, new BigVector.Factory().fromVector(m.getRow(i)));
			}

			return p;
		}
	}
}
