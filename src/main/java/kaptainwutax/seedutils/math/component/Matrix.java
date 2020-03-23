package kaptainwutax.seedutils.math.component;

import kaptainwutax.seedutils.math.component.number.NumberType;

public class Matrix<T extends NumberType<?, T>> implements IComponent<Matrix<T>> {

	protected final GenGrid<T> grid;

	public Matrix(int height, int width) {
		this.grid = new GenGrid<>(height, width);
	}

	public int getHeight() {
		return this.grid.getHeight();
	}

	public int getWidth() {
		return this.grid.getWidth();
	}

	public T get(int i, int j) {
		return this.grid.get(i, j);
	}

	public void set(int i, int j, T value) {
		this.grid.set(i, j, value);
	}

	public void swap(int i, int j) {
		this.grid.swap(i, j);
	}

	public Matrix<T> add(Matrix<T> m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			//TODO: bad things
		}

		Matrix<T> p = new Matrix<>(this.getHeight(), m.getWidth());

		for(int i = 0; i < this.getHeight(); i++) {
			for(int j = 0; j < this.getWidth(); j++) {
				p.set(i, j, this.get(i, j).add(m.get(i, j)));
			}
		}

		return p;
	}

	public Matrix<T> subtract(Matrix<T> m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			//TODO: bad things
		}

		Matrix<T> p = new Matrix<>(this.getHeight(), m.getWidth());

		for(int i = 0; i < this.getHeight(); i++) {
			for(int j = 0; j < this.getWidth(); j++) {
				p.set(i, j, this.get(i, j).subtract(m.get(i, j)));
			}
		}

		return p;
	}

	public Matrix<T> multiply(Matrix<T> m) {
		if(this.getWidth() != m.getHeight()) {
			//TODO: bad things
		}

		Matrix<T> p = new Matrix<>(this.getHeight(), m.getWidth());

		for(int i = 1; i <= p.getHeight(); i++) {
			for(int j = 1; j <= p.getWidth(); j++) {
				for(int k = 1; k <= this.getWidth(); k++) {
					p.get(i, j).addEquals(this.get(i, j).multiply(m.get(k, j)));
				}
			}
		}

		return p;
	}

	@Override
	public Matrix<T> divide(Matrix<T> m) {
		//TODO: bad things
		return null;
	}

	@Override
	public void addEquals(Matrix<T> m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			//TODO: bad things
		}

		for(int i = 0; i < this.getHeight(); i++) {
			for(int j = 0; j < this.getWidth(); j++) {
				this.get(i, j).addEquals(m.get(i, j));
			}
		}
	}

	@Override
	public void subtractEquals(Matrix<T> m) {
		if(this.getHeight() != m.getHeight() || this.getWidth() != m.getWidth()) {
			//TODO: bad things
		}

		for(int i = 0; i < this.getHeight(); i++) {
			for(int j = 0; j < this.getWidth(); j++) {
				this.get(i, j).subtractEquals(m.get(i, j));
			}
		}
	}

	@Override
	public void multiplyEquals(Matrix<T> m) {
		if(this.getWidth() != m.getHeight()) {
			//TODO: bad things
		}

		for(int i = 1; i <= this.getHeight(); i++) {
			for(int j = 1; j <= m.getWidth(); j++) {
				this.get(i, j).setToZero();

				for(int k = 1; k <= this.getWidth(); k++) {
					T n = this.get(i, j).copy();
					this.get(i, j).multiplyEquals(m.get(k, j));
					this.get(i, j).addEquals(n);
				}
			}
		}
	}

	@Override
	public void divideEquals(Matrix<T> a) {
		//TODO: bad things
	}

	public Matrix<T> transpose() {
		Matrix<T> p = new Matrix<T>(this.getWidth(), this.getHeight());

		for(int i = 0; i < this.getHeight(); i++) {
			for(int j = 0; j < this.getWidth(); j++) {
				p.set(j, i, this.get(i, j).copy());
			}
		}

		return p;
	}

	@Override
	public Matrix<T> copy() {
		Matrix<T> m = new Matrix<>(this.getHeight(), this.getWidth());

		for(int i = 0; i < this.getHeight(); i++) {
			for(int j = 0; j < this.getWidth(); j++) {
				m.set(i, j, this.get(i, j).copy());
			}
		}

		return m;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");

		for(int i = 0; i < this.getHeight(); i++) {
			sb.append("{");
			for(int j = 0; j < this.getWidth(); j++) {
				sb.append(this.get(i, j)).append(j == this.getWidth() - 1 ? "" : ", ");
			}
			sb.append("}").append(i == this.getHeight() - 1 ? "" : ", ");
		}

		return sb.append("}").toString();
	}

}
