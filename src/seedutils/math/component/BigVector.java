package seedutils.math.component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

public class BigVector implements ICopy<BigVector> {

	private BigDecimal[] numbers;
	private int length;

	public BigVector(int length) {
		this.length = length;
		this.numbers = new BigDecimal[this.length];
	}

	public BigVector(BigDecimal[] numbers) {
		this.length = numbers.length;
		this.numbers = new BigDecimal[this.length];

		for(int i = 0; i < this.length; i++) {
			this.set(i, numbers[i]);
		}
	}

	public int getLength() {
		return this.length;
	}

	public BigDecimal get(int i) {
		return this.numbers[i];
	}

	public void set(int i, BigDecimal value) {
		this.numbers[i] = value.setScale(20, RoundingMode.HALF_UP);
	}

	public BigDecimal magnitudeSq() {
		BigDecimal magnitude = BigDecimal.ZERO;

		for(int i = 0; i < this.getLength(); i++) {
			magnitude = magnitude.add(this.get(i).multiply(this.get(i)));
		}

		return magnitude;
	}

	public BigVector add(BigVector a) {
		BigVector v = new BigVector(this.getLength());

		for(int i = 0; i < v.getLength(); i++) {
			v.set(i, this.get(i).add(a.get(i)));
		}

		return v;
	}

	public BigVector subtract(BigVector a) {
		BigVector v = new BigVector(this.getLength());

		for(int i = 0; i < v.getLength(); i++) {
			v.set(i, this.get(i).subtract(a.get(i)));
		}

		return v;
	}

	public BigVector scale(BigDecimal scalar) {
		BigVector v = this.copy();

		for(int i = 0; i < v.getLength(); i++) {
			v.set(i, this.get(i).multiply(scalar));
		}

		return v;
	}

	public void addEquals(BigVector a) {
		for(int i = 0; i < this.getLength(); i++) {
			this.set(i, this.get(i).add(a.get(i)));
		}
	}

	public void subtractEquals(BigVector a) {
		for(int i = 0; i < this.getLength(); i++) {
			this.set(i, this.get(i).subtract(a.get(i)));
		}
	}

	public void scaleEquals(BigDecimal scalar) {
		for(int i = 0; i < this.getLength(); i++) {
			this.set(i, this.get(i).multiply(scalar));
		}
	}

	public BigDecimal dot(BigVector v) {
		BigDecimal dot = BigDecimal.ZERO;

		for(int i = 0; i < this.getLength(); i++) {
			dot = dot.add(this.get(i).multiply(v.get(i)));
		}

		return dot;
	}

	public BigDecimal getScalarProjection(BigVector v) {
		return this.dot(v).divide(v.magnitudeSq(), RoundingMode.HALF_UP);
	}

	public BigVector projectOnto(BigVector v) {
		return v.scale(this.getScalarProjection(v));
	}

	@Override
	public BigVector copy() {
		BigVector v = new BigVector(this.getLength());

		for(int i = 0; i < v.getLength(); i++) {
			v.set(i, this.get(i));
		}

		return v;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");

		for(int i = 0; i < this.getLength(); i++) {
			sb.append(this.get(i) == null ? null : this.get(i).stripTrailingZeros().toPlainString())
					.append(i == this.getLength() - 1 ? "" : ", ");
		}

		return sb.append("}").toString();
	}

	public static class Builder {
		private int length;
		private BigDecimal defaultValue;

		public Builder setLength(int length) {
			this.length = length;
			return this;
		}

		public Builder fillWith(BigDecimal defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public BigVector build() {
			BigVector v = new BigVector(this.length);

			if(this.defaultValue != null) {
				for(int i = 0; i < v.getLength(); i++) {
					v.set(i, this.defaultValue);
				}
			}

			return v;
		}
	}

	public static class Factory {
		public BigVector fromString(String raw) {
			raw = raw.replaceAll("\\s+","");

			String[] data = raw.split(Pattern.quote(","));
			BigVector v = new BigVector(data.length);

			for(int i = 0; i < data.length; i++) {
				v.set(i, new BigDecimal(data[i]));
			}

			return v;
		}

		public BigVector fromVector(Vector v) {
			BigVector p = new BigVector(v.getLength());

			for(int i = 0; i < p.getLength(); i++) {
				p.set(i, BigDecimal.valueOf(v.get(i)));
			}

			return p;
		}
	}
}
