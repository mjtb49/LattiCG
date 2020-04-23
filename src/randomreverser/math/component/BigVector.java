package randomreverser.math.component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.regex.Pattern;

public final class BigVector {

	private BigDecimal[] numbers;
	private int dimension;

	public BigVector(int dimension) {
		this.dimension = dimension;
		this.numbers = new BigDecimal[this.dimension];
	}

	public BigVector(double... numbers) {
		this(toBigDecimals(numbers));
	}

	private static BigDecimal[] toBigDecimals(double[] numbers) {
		BigDecimal[] decimals = new BigDecimal[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			decimals[i] = BigDecimal.valueOf(numbers[i]);
		}
		return decimals;
	}

	public BigVector(BigDecimal... numbers) {
		this.dimension = numbers.length;
		this.numbers = new BigDecimal[this.dimension];

		for(int i = 0; i < this.dimension; i++) {
			this.set(i, numbers[i]);
		}
	}

	public int getDimension() {
		return this.dimension;
	}

	public BigDecimal get(int i) {
		return this.numbers[i];
	}

	public void set(int i, BigDecimal value) {
		this.numbers[i] = value.setScale(20, RoundingMode.HALF_UP);
	}

	public BigDecimal magnitudeSq() {
		BigDecimal magnitude = BigDecimal.ZERO;

		for(int i = 0; i < this.getDimension(); i++) {
			magnitude = magnitude.add(this.get(i).multiply(this.get(i)));
		}

		return magnitude;
	}

	public boolean isZero() {
		for(int i = 0; i < this.getDimension(); i++) {
			if(this.get(i).compareTo(BigDecimal.ZERO) != 0) return false;
		}

		return true;
	}

	public BigVector add(BigVector a) {
		assertSameDimension(a);

		BigVector v = new BigVector(this.getDimension());

		for(int i = 0; i < v.getDimension(); i++) {
			v.set(i, this.get(i).add(a.get(i)));
		}

		return v;
	}

	public BigVector subtract(BigVector a) {
		assertSameDimension(a);

		BigVector v = new BigVector(this.getDimension());

		for(int i = 0; i < v.getDimension(); i++) {
			v.set(i, this.get(i).subtract(a.get(i)));
		}

		return v;
	}

	public BigVector multiply(BigDecimal scalar) {
		BigVector v = this.copy();

		for(int i = 0; i < v.getDimension(); i++) {
			v.set(i, this.get(i).multiply(scalar));
		}

		return v;
	}

	public BigVector divide(BigDecimal scalar) {
		BigVector v = this.copy();

		for(int i = 0; i < v.getDimension(); i++) {
			v.set(i, this.get(i).divide(scalar, RoundingMode.HALF_UP));
		}

		return v;
	}

	public BigVector addEquals(BigVector a) {
		assertSameDimension(a);

		for(int i = 0; i < this.getDimension(); i++) {
			this.set(i, this.get(i).add(a.get(i)));
		}

		return this;
	}

	public BigVector subtractEquals(BigVector a) {
		assertSameDimension(a);

		for(int i = 0; i < this.getDimension(); i++) {
			this.set(i, this.get(i).subtract(a.get(i)));
		}

		return this;
	}

	public BigVector multiplyEquals(BigDecimal scalar) {
		for(int i = 0; i < this.getDimension(); i++) {
			this.set(i, this.get(i).multiply(scalar));
		}

		return this;
	}

	public BigVector divideEquals(BigDecimal scalar) {
		for(int i = 0; i < this.getDimension(); i++) {
			this.set(i, this.get(i).divide(scalar, RoundingMode.HALF_UP));
		}

		return this;
	}

	public BigDecimal dot(BigVector v) {
		assertSameDimension(v);

		BigDecimal dot = BigDecimal.ZERO;

		for(int i = 0; i < this.getDimension(); i++) {
			dot = dot.add(this.get(i).multiply(v.get(i)));
		}

		return dot;
	}

	public BigDecimal gramSchmidtCoefficient(BigVector v) {
		return this.dot(v).divide(v.magnitudeSq(), RoundingMode.HALF_UP);
	}

	public BigVector projectOnto(BigVector v) {
		return v.multiply(this.gramSchmidtCoefficient(v));
	}

	public BigVector copy() {
		BigVector v = new BigVector(this.getDimension());

		for(int i = 0; i < v.getDimension(); i++) {
			v.set(i, this.get(i) == null ? BigDecimal.ZERO : this.get(i));
		}

		return v;
	}

	private void assertSameDimension(BigVector other) {
		if (other.dimension != this.dimension) {
			throw new IllegalArgumentException("The other vector is not the same dimension");
		}
	}

	public boolean equals(BigVector other, BigDecimal tolerance) {
		if (this.dimension != other.dimension) {
			return false;
		}
		for (int i = 0; i < dimension; i++) {
			if (this.get(i).subtract(other.get(i)).abs().compareTo(tolerance) > 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(numbers);
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) return true;
		if (other == null || other.getClass() != BigVector.class) return false;
		BigVector that = (BigVector) other;
		return this.equals(that, BigDecimal.ZERO);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");

		for(int i = 0; i < this.getDimension(); i++) {
			sb.append(this.get(i) == null ? null : this.get(i).stripTrailingZeros().toPlainString())
					.append(i == this.getDimension() - 1 ? "" : ", ");
		}

		return sb.append("}").toString();
	}

	public static BigVector fromString(String raw) {
		raw = raw.replaceAll("\\s+","");

		String[] data = raw.split(Pattern.quote(","));
		BigVector v = new BigVector(data.length);

		for(int i = 0; i < data.length; i++) {
			v.set(i, new BigDecimal(data[i]));
		}

		return v;
	}

	public static BigVector fromVector(Vector v) {
		BigVector p = new BigVector(v.getDimension());

		for(int i = 0; i < p.getDimension(); i++) {
			p.set(i, BigDecimal.valueOf(v.get(i)));
		}

		return p;
	}
}
