package kaptainwutax.seedutils.math.decomposition;

import kaptainwutax.seedutils.math.component.BigMatrix;
import kaptainwutax.seedutils.math.component.BigVector;

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
		this.newBasis = new BigMatrix(this.basis.getHeight(), this.basis.getWidth());
		this.coefficients = new BigMatrix(this.basis.getHeight(), this.basis.getHeight() - 1);

		for(int i = 0; i < this.basis.getHeight(); i++) {
			BigVector v = this.basis.getRow(i).copy();

			for(int j = 0; j < i; j++) {
				BigDecimal scalar = this.basis.getRow(i).getScalarProjection(this.newBasis.getRow(j));
				v.subtractEquals(this.newBasis.getRow(j).scale(scalar));
				this.coefficients.set(i, j, scalar);
			}

			this.newBasis.setRow(i, v);
		}
	}

}
