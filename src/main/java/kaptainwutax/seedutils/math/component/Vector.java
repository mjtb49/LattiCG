package kaptainwutax.seedutils.math.component;

import kaptainwutax.seedutils.math.component.number.NumberType;

public class Vector<T extends NumberType<?, T>> implements IComponent<Vector<T>> {

	private GenGrid<T> numbers;

	public Vector(int length) {
		this.numbers = new GenGrid<>(length, 1);
	}

	public Vector(NumberType<?, ?>... numbers) {
		this.numbers = new GenGrid<>(numbers.length, 1);
		for(int i = 0; i < numbers.length; i++)this.set(i, (T)numbers[i]);
	}

	public int getLength() {
		return this.numbers.getHeight();
	}

	public T get(int i) {
		return this.numbers.get(i, 0);
	}

	public void set(int i, T value) {
		this.numbers.set(i, 0, value);
	}

	public T getMagnitudeSq() {
		T result = this.get(0).getZero();

		for(int i = 0; i < this.getLength(); i++) {
			result.addEquals(this.get(i).multiply(this.get(i)));
		}

		return result;
	}

	@Override
	public Vector<T> add(Vector<T> v) {
		if(this.getLength() != v.getLength()) {
			//TODO: bad things
		}

		Vector<T> p = new Vector<>(this.getLength());

		for(int i = 0; i < this.getLength(); i++) {
			p.set(i, this.get(i).add(v.get(i)));
		}

		return p;
	}

	@Override
	public Vector<T> subtract(Vector<T> v) {
		if(this.getLength() != v.getLength()) {
			//TODO: bad things
		}

		Vector<T> p = new Vector<>(this.getLength());

		for(int i = 0; i < this.getLength(); i++) {
			p.set(i, this.get(i).subtract(v.get(i)));
		}

		return p;
	}

	@Override
	public Vector<T> multiply(Vector<T> v) {
		//TODO: bad things
		return null;
	}

	@Override
	public Vector<T> divide(Vector<T> v) {
		//TODO: bad things
		return null;
	}

	@Override
	public void addEquals(Vector<T> v) {
		if(this.getLength() != v.getLength()) {
			//TODO: bad things
		}

		for(int i = 0; i < this.getLength(); i++) {
			this.get(i).addEquals(v.get(i));
		}
	}

	@Override
	public void subtractEquals(Vector<T> v) {
		if(this.getLength() != v.getLength()) {
			//TODO: bad things
		}

		for(int i = 0; i < this.getLength(); i++) {
			this.get(i).subtractEquals(v.get(i));
		}
	}

	@Override
	public void multiplyEquals(Vector<T> a) {
		//TODO: bad things
	}

	@Override
	public void divideEquals(Vector<T> a) {
		//TODO: bad things
	}

	public Vector<T> scale(T scalar) {
		Vector<T> p = new Vector<>(this.getLength());

		for(int i = 0; i < this.getLength(); i++) {
			p.set(i, this.get(i).multiply(scalar));
		}

		return p;
	}

	public T dot(Vector<T> v) {
		if(this.getLength() != v.getLength()) {
			//TODO: bad things
		}

		T dot = this.get(0).getZero();

		for(int i = 0; i < this.getLength(); i++) {
			dot.addEquals(this.get(i).multiply(v.get(i)));
		}

		return dot;
	}

	public Vector<T> projectOnto(Vector<T> v) {
		return v.scale(this.getScalarProjection(v));
	}

	public T getScalarProjection(Vector<T> v) {
		return this.dot(v).divide(v.getMagnitudeSq());
	}

	@Override
	public Vector<T> copy() {
		Vector<T> v = new Vector<>(this.getLength());

		for(int i = 0; i < this.getLength(); i++) {
			v.set(i, this.get(i).copy());
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

}
