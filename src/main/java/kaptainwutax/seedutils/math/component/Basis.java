package kaptainwutax.seedutils.math.component;

import kaptainwutax.seedutils.math.component.number.NumberType;
import kaptainwutax.seedutils.math.decomposition.GramSchmidt;

public class Basis<T extends NumberType<?, T>> extends Matrix<T> {

	public Basis(int height, int width) {
		super(height, width);
	}

	public Basis(Vector<?>... vectors) {
		super(vectors.length, vectors[0].getLength());

		for(int i = 0; i < this.getHeight(); i++) {
			for(int j = 0; j < this.getWidth(); j++) {
				this.set(i, j, ((Vector<T>)vectors[i]).get(j));
			}
		}
	}

	public int getLength() {
		return this.getHeight();
	}

	public Vector<T> getVector(int i) {
		Vector<T> vector = new Vector<>(this.getWidth());

		for(int j = 0; j < this.getWidth(); j++) {
			vector.set(j, this.get(i, j));
		}

		return vector;
	}

	public void setVector(int i, Vector<T> vector) {
		for(int j = 0; j < this.getWidth(); j++) {
			this.set(i, j, vector.get(j));
		}
	}

	public boolean isOrthogonal() {
		for(int i = 0; i < this.getLength(); i++) {
			for(int j = 0; j < this.getLength(); j++) {
				if(i == j)continue;

				if(this.getVector(i).dot(this.getVector(j)).isZero()) {
					return true;
				}
			}
		}

		return false;
	}

	public GramSchmidt<T> getGramSchmidt() {
		Basis<T> vBasis = new Basis<T>(this.getHeight(), this.getWidth());
		Matrix<T> coefficients = new Matrix<>(this.getLength(), this.getLength() - 1);

		for(int i = 0; i < this.getLength(); i++) {
			Vector<T> v = this.getVector(i).copy();

			for(int j = 0; j < i; j++) {
				T scalar = this.getVector(i).getScalarProjection(vBasis.getVector(j));
				v.subtractEquals(vBasis.getVector(j).scale(scalar));
				coefficients.set(i, j, scalar);
			}

			vBasis.setVector(i, v);
		}

		return new GramSchmidt<>(vBasis, coefficients);
	}

	@Override
	public Basis<T> copy() {
		Basis<T> p = new Basis<>(this.getLength(), this.getWidth());

		for(int i = 0; i < this.getLength(); i++) {
			p.setVector(i, this.getVector(i).copy());
		}

		return p;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");

		for(int i = 0; i < this.getLength(); i++) {
			sb.append(this.getVector(i));
		}

		return sb.append("}").toString();
	}

}
