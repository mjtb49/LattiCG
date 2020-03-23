package kaptainwutax.seedutils.math.decomposition;

import kaptainwutax.seedutils.math.component.Basis;
import kaptainwutax.seedutils.math.component.Matrix;
import kaptainwutax.seedutils.math.component.number.NumberType;

public class GramSchmidt<T extends NumberType<?, T>> {

	private Basis<T> basis;
	private Matrix<T> coefficients;

	public GramSchmidt(Basis<T> basis, Matrix<T> coefficients) {
		this.basis = basis;
		this.coefficients = coefficients;
	}

	public Basis<T> getBasis() {
		return this.basis;
	}

	public Matrix<T> getCoefficients() {
		return this.coefficients;
	}

}
