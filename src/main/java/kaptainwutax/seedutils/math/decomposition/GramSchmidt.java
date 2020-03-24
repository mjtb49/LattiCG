package kaptainwutax.seedutils.math.decomposition;

import kaptainwutax.seedutils.math.component.Matrix;
import kaptainwutax.seedutils.math.component.Vector;

public class GramSchmidt {

	private Matrix basis;

	private Matrix newBasis;
	private Matrix coefficients;

	public GramSchmidt(Matrix basis) {
		this.basis = basis;
	}

	public GramSchmidt(Matrix newBasis, Matrix coefficients) {
		this.newBasis = newBasis;
		this.coefficients = coefficients;
	}

	public Matrix getBasis() {
		return this.basis;
	}

	public Matrix getNewBasis() {
		return this.newBasis;
	}

	public Matrix getCoefficients() {
		return this.coefficients;
	}

	public void compute() {
		this.newBasis = new Matrix(this.basis.getHeight(), this.basis.getWidth());
		this.coefficients = new Matrix(this.basis.getHeight(), this.basis.getHeight() - 1);

		for(int i = 0; i < this.basis.getHeight(); i++) {
			Vector v = this.basis.getRow(i).copy();

			for(int j = 0; j < i; j++) {
				double scalar = this.basis.getRow(i).getScalarProjection(this.newBasis.getRow(j));
				v.subtractEquals(this.newBasis.getRow(j).scale(scalar));
				this.coefficients.set(i, j, scalar);
			}

			this.newBasis.setRow(i, v);
		}
	}

}
