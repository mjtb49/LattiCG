package seedutils.math.decomposition;

import seedutils.math.component.Matrix;
import seedutils.math.component.Vector;

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
		this.coefficients = new Matrix(this.basis.getHeight(), this.basis.getWidth());

		for(int i = 0; i < this.basis.getHeight(); i++) {
			Vector v = this.basis.getRow(i).copy();
			this.newBasis.setRow(i, v);

			for(int j = 0; j < i; j++) {
				double scalar = this.basis.getRow(i).getScalarProjection(this.newBasis.getRow(j));
				v.subtractEquals(this.newBasis.getRow(j).scale(scalar));
				this.coefficients.set(i, j, scalar);
			}
		}

		for(int i = 0; i < this.basis.getHeight(); i++) {
			for(int j = i; j < this.basis.getWidth(); j++) {
				this.coefficients.set(i, j, this.basis.getRow(i).getScalarProjection(this.newBasis.getRow(j)));
			}
		}
	}

}
