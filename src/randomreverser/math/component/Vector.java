package randomreverser.math.component;

import java.util.Arrays;
import java.util.function.IntToDoubleFunction;
import java.util.regex.Pattern;

public final class Vector {

	private double[] numbers;
	private int dimension;

	public Vector(int dimension) {
		this.dimension = dimension;
		this.numbers = new double[this.dimension];
	}

	public Vector(double... numbers) {
		this.numbers = numbers;
		this.dimension = this.numbers.length;
	}

	public Vector(int dimension, IntToDoubleFunction generator) {
		this.dimension = dimension;
		this.numbers = new double[this.dimension];

		for(int i = 0; i < this.getDimension(); i++) {
			this.set(i, generator.applyAsDouble(i));
		}
	}

	public int getDimension() {
		return this.dimension;
	}

	public double get(int i) {
		return this.numbers[i];
	}

	public void set(int i, double value) {
		this.numbers[i] = value;
	}

	public double magnitude() {
		return Math.sqrt(magnitudeSq());
	}

	public double magnitudeSq() {
		double magnitude = 0.0D;

		for(int i = 0; i < this.getDimension(); i++) {
			magnitude += this.get(i) * this.get(i);
		}

		return magnitude;
	}

	public boolean isZero() {
		for(int i = 0; i < this.getDimension(); i++) {
			if(this.get(i) != 0.0D) return false;
		}

		return true;
	}

	public Vector add(Vector a) {
		assertSameDimension(a);

		Vector v = new Vector(this.getDimension());

		for(int i = 0; i < v.getDimension(); i++) {
			v.set(i, this.get(i) + a.get(i));
		}

		return v;
	}

	public Vector subtract(Vector a) {
		assertSameDimension(a);

		Vector v = new Vector(this.getDimension());

		for(int i = 0; i < v.getDimension(); i++) {
			v.set(i, this.get(i) - a.get(i));
		}

		return v;
	}

	public Vector multiply(double scalar) {
		Vector v = this.copy();

		for(int i = 0; i < v.getDimension(); i++) {
			v.set(i, this.get(i) * scalar);
		}

		return v;
	}

	public Vector multiply(Matrix m) {
		if(this.getDimension() != m.getRowCount()) {
			throw new IllegalArgumentException("Vector dimension should equal the number of matrix rows");
		}

		Vector v = new Vector(m.getColumnCount());

		for(int i = 0; i < v.getDimension(); i++) {
			v.set(i, this.dot(m.getColumn(i)));
		}

		return v;
	}

	public Vector divide(double scalar) {
		Vector v = this.copy();

		for(int i = 0; i < v.getDimension(); i++) {
			v.set(i, this.get(i) / scalar);
		}

		return v;
	}

	public Vector addEquals(Vector a) {
		assertSameDimension(a);

		for(int i = 0; i < this.getDimension(); i++) {
			this.set(i, this.get(i) + a.get(i));
		}

		return this;
	}

	public Vector subtractEquals(Vector a) {
		assertSameDimension(a);

		for(int i = 0; i < this.getDimension(); i++) {
			this.set(i, this.get(i) - a.get(i));
		}

		return this;
	}

	public Vector multiplyEquals(double scalar) {
		for(int i = 0; i < this.getDimension(); i++) {
			this.set(i, this.get(i) * scalar);
		}

		return this;
	}

	public Vector divideEquals(double scalar) {
		for(int i = 0; i < this.getDimension(); i++) {
			this.set(i, this.get(i) / scalar);
		}

		return this;
	}

	public double dot(Vector v) {
		assertSameDimension(v);

		double dot = 0.0D;

		for(int i = 0; i < this.getDimension(); i++) {
			dot += this.get(i) * v.get(i);
		}

		return dot;
	}

	public double gramSchmidtCoefficient(Vector v) {
		return this.dot(v) / v.magnitudeSq();
	}

	public Vector projectOnto(Vector v) {
		return v.multiply(this.gramSchmidtCoefficient(v));
	}

	public Vector copy() {
		Vector v = new Vector(this.getDimension());

		for(int i = 0; i < v.getDimension(); i++) {
			v.set(i, this.get(i));
		}

		return v;
	}

	private void assertSameDimension(Vector other) {
		if (other.dimension != this.dimension) {
			throw new IllegalArgumentException("The other vector is not the same dimension");
		}
	}

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
		return Arrays.hashCode(numbers);
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) return true;
		if (other == null || other.getClass() != Vector.class) return false;
		Vector that = (Vector) other;
		return Arrays.equals(this.numbers, that.numbers);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");

		for(int i = 0; i < this.getDimension(); i++) {
			sb.append(this.get(i)).append(i == this.getDimension() - 1 ? "" : ", ");
		}

		return sb.append("}").toString();
	}

	public static Vector fromString(String raw) {
		raw = raw.replaceAll("\\s+","");

		String[] data = raw.split(Pattern.quote(","));
		Vector v = new Vector(data.length);

		for(int i = 0; i < data.length; i++) {
			v.set(i, Double.parseDouble(data[i]));
		}

		return v;
	}

	public static Vector fromBigVector(BigVector v) {
		Vector p = new Vector(v.getDimension());

		for(int i = 0; i < p.getDimension(); i++) {
			p.set(i, v.get(i).doubleValue());
		}

		return p;
	}
}
