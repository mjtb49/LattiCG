package randomreverser.math.decomposition;

import randomreverser.math.component.Matrix;
import randomreverser.math.component.Vector;

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
		this.newBasis = new Matrix(this.basis.getRowCount(), this.basis.getColumnCount());
		this.coefficients = new Matrix(this.basis.getRowCount(), this.basis.getColumnCount());

		for(int i = 0; i < this.basis.getRowCount(); i++) {
			Vector v = this.basis.getRow(i).copy();
			this.newBasis.setRow(i, v);

			for(int j = 0; j < i; j++) {
				double scalar = this.basis.getRow(i).gramSchmidtCoefficient(this.newBasis.getRow(j));
				v.subtractEquals(this.newBasis.getRow(j).multiply(scalar));
				this.coefficients.set(i, j, scalar);
			}
		}

		for(int i = 0; i < this.basis.getRowCount(); i++) {
			for(int j = i; j < this.basis.getColumnCount(); j++) {
				this.coefficients.set(i, j, this.basis.getRow(i).gramSchmidtCoefficient(this.newBasis.getRow(j)));
			}
		}
	}

}
