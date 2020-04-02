package seedutils.math.component;

import java.util.function.Function;
import java.util.regex.Pattern;

public class Vector implements ICopy<Vector> {

	private double[] numbers;
	private int length;

	public Vector(int length) {
		this.length = length;
		this.numbers = new double[this.length];
	}

	public Vector(double[] numbers) {
		this.numbers = numbers;
		this.length = this.numbers.length;
	}

	public Vector(int length, Function<Integer, Double> generator) {
		this.length = length;
		this.numbers = new double[this.length];

		for(int i = 0; i < this.getLength(); i++) {
			this.set(i, generator.apply(i));
		}
	}

	public int getLength() {
		return this.length;
	}

	public double get(int i) {
		return this.numbers[i];
	}

	public void set(int i, double value) {
		this.numbers[i] = value;
	}

	public double magnitudeSq() {
		double magnitude = 0.0D;

		for(int i = 0; i < this.getLength(); i++) {
			magnitude += this.get(i) * this.get(i);
		}

		return magnitude;
	}

	public boolean isZero() {
		for(int i = 0; i < this.getLength(); i++) {
			if(this.get(i) != 0.0D)return false;
		}

		return true;
	}

	public Vector add(Vector a) {
		Vector v = new Vector(this.getLength());

		for(int i = 0; i < v.getLength(); i++) {
			v.set(i, this.get(i) + a.get(i));
		}

		return v;
	}

	public Vector subtract(Vector a) {
		Vector v = new Vector(this.getLength());

		for(int i = 0; i < v.getLength(); i++) {
			v.set(i, this.get(i) - a.get(i));
		}

		return v;
	}

	public Vector multiply(double scalar) {
		Vector v = this.copy();

		for(int i = 0; i < v.getLength(); i++) {
			v.set(i, this.get(i) * scalar);
		}

		return v;
	}

	public Vector multiply(Matrix m) {
		if(this.getLength() != m.getHeight()) {
			throw new UnsupportedOperationException("Vector length should equal the matrix height");
		}

		Vector v = new Vector(m.getWidth());

		for(int i = 0; i < v.getLength(); i++) {
			v.set(i, this.dot(m.getRow(i)));
		}

		return v;
	}

	public Vector divide(double scalar) {
		Vector v = this.copy();

		for(int i = 0; i < v.getLength(); i++) {
			v.set(i, this.get(i) / scalar);
		}

		return v;
	}

	public void addEquals(Vector a) {
		for(int i = 0; i < this.getLength(); i++) {
			this.set(i, this.get(i) + a.get(i));
		}
	}

	public void subtractEquals(Vector a) {
		for(int i = 0; i < this.getLength(); i++) {
			this.set(i, this.get(i) - a.get(i));
		}
	}

	public void multiplyEquals(double scalar) {
		for(int i = 0; i < this.getLength(); i++) {
			this.set(i, this.get(i) * scalar);
		}
	}

	public void divideEquals(double scalar) {
		for(int i = 0; i < this.getLength(); i++) {
			this.set(i, this.get(i) / scalar);
		}
	}

	public double dot(Vector v) {
		double dot = 0.0D;

		for(int i = 0; i < this.getLength(); i++) {
			dot += this.get(i) * v.get(i);
		}

		return dot;
	}

	public double getScalarProjection(Vector v) {
		return this.dot(v) / v.magnitudeSq();
	}

	public Vector projectOnto(Vector v) {
		return v.multiply(this.getScalarProjection(v));
	}

	@Override
	public Vector copy() {
		Vector v = new Vector(this.getLength());

		for(int i = 0; i < v.getLength(); i++) {
			v.set(i, this.get(i));
		}

		return v;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");

		for(int i = 0; i < this.getLength(); i++) {
			sb.append(this.get(i)).append(i == this.getLength() - 1 ? "" : ", ");
		}

		return sb.append("}").toString();
	}

	public static class Builder {
		private int length;
		private double defaultValue;

		public Builder setLength(int length) {
			this.length = length;
			return this;
		}

		public Builder fillWith(double defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public Vector build() {
			Vector v = new Vector(this.length);

			if(this.defaultValue != 0.0D) {
				for(int i = 0; i < v.getLength(); i++) {
					v.set(i, this.defaultValue);
				}
			}

			return v;
		}
	}

	public static class Factory {
		public Vector fromString(String raw) {
			raw = raw.replaceAll("\\s+","");

			String[] data = raw.split(Pattern.quote(","));
			Vector v = new Vector(data.length);

			for(int i = 0; i < data.length; i++) {
				v.set(i, Double.parseDouble(data[i]));
			}

			return v;
		}

		public Vector fromBigVector(BigVector v) {
			Vector p = new Vector(v.getLength());

			for(int i = 0; i < p.getLength(); i++) {
				p.set(i, v.get(i).doubleValue());
			}

			return p;
		}
	}
}
