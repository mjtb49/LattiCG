package randomreverser.math.decomposition;

import randomreverser.math.component.BigMatrix;
import randomreverser.math.component.BigVector;

import java.math.BigDecimal;

public class BigGramSchmidt {

	private BigMatrix basis;

	private BigMatrix newBasis;
	private BigMatrix coefficients;

	public BigGramSchmidt(BigMatrix basis) {
		this.basis = basis;
	}

	public BigGramSchmidt(BigMatrix newBasis, BigMatrix coefficients) {
		this.newBasis = newBasis;
		this.coefficients = coefficients;
	}

	public BigMatrix getBasis() {
		return this.basis;
	}

	public BigMatrix getNewBasis() {
		return this.newBasis;
	}

	public BigMatrix getCoefficients() {
		return this.coefficients;
	}

	public void compute() {
		this.newBasis = new BigMatrix(this.basis.getRowCount(), this.basis.getColumnCount());
		this.coefficients = new BigMatrix(this.basis.getRowCount(), this.basis.getColumnCount());

		for(int i = 0; i < this.basis.getRowCount(); i++) {
			BigVector v = this.basis.getRow(i).copy();
			this.newBasis.setRow(i, v);

			for(int j = 0; j < i; j++) {
				BigDecimal scalar = this.basis.getRow(i).gramSchmidtCoefficient(this.newBasis.getRow(j));
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
