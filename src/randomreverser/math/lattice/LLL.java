package randomreverser.math.lattice;

import randomreverser.math.component.BigMatrix;
import randomreverser.math.component.BigFraction;
import randomreverser.math.component.BigVector;
import randomreverser.math.component.Matrix;

public class LLL {

	private LLL() {
	}

	private BigMatrix gramSchmidtBasis;
	private BigMatrix mu;
	private BigMatrix lattice;
	private BigMatrix H;
	private BigFraction[] sizes;
	private Params params;
	private int kmax;
	private int k;
	private boolean shouldUpdateGramSchmidt;
	private static final BigFraction eta = BigFraction.HALF;


    /**
     * LLL lattice reduction implemented as described on page 95 of Henri Cohen's
     * "A course in computational number theory"
     * @param lattice the lattice to reduce
     * @param params the parameters to be passed to LLL
     * @return the reduced lattice
     */
	public static Result reduce(BigMatrix lattice, Params params) {
		return new LLL().reduce0(lattice, params);
	}

	private Result reduce0(BigMatrix lattice, Params params) {
		this.params = params;
		int n = lattice.getRowCount();
		int m = lattice.getColumnCount();
		gramSchmidtBasis = new BigMatrix(n,m);
		mu = new BigMatrix(n,n);
		k = 1;
		kmax = 0;
		gramSchmidtBasis.setRow(0, lattice.getRow(0).copy());
		shouldUpdateGramSchmidt = true;
		H = BigMatrix.identityMatrix(n);
		this.lattice = lattice.copy();
		sizes = new BigFraction[n];
		sizes[0] = this.lattice.getRow(0).magnitudeSq();

		while (k < n) {
			if (k > kmax && shouldUpdateGramSchmidt) {
				kmax = k;
				incGramSchmidt();
			}
			testCondition();
		}

		int p = 0;
		for (int i = 0; i < n; i++) {
			if (this.lattice.getRow(i).isZero()) {
				p++;
			}
		}

		BigMatrix nonZeroLattice = new BigMatrix(n-p,m);
		for (int i = p; i < n; i++) {
			nonZeroLattice.setRow(i-p,this.lattice.getRow(i));
		}
		return new Result(p,nonZeroLattice,H);
	}

	private void incGramSchmidt() {
		for (int j = 0; j <= k - 1; j++) {
			if (sizes[j].compareTo(BigFraction.ZERO) != 0) {
				mu.set(k, j, lattice.getRow(k).dot(gramSchmidtBasis.getRow(j)).divide(sizes[j]));
			} else {
				mu.set(k, j, BigFraction.ZERO);
			}
		}
		BigVector newRow = lattice.getRow(k).copy();
		for(int i = 0; i <= k - 1; i++) {
			newRow.subtractEquals(gramSchmidtBasis.getRow(i).multiply(mu.get(k,i)));
		}
		gramSchmidtBasis.setRow(k,newRow);
		sizes[k] = newRow.magnitudeSq();
	}

	private void testCondition() {
		red(k,k-1);
		if (sizes[k].toDouble() < ((params.delta - (mu.get(k,k-1).multiply(mu.get(k,k-1))).toDouble())* (sizes[k-1]).toDouble())) { //TODO I don't trust this comparison as doubles
		    swapg(k);
			k = Math.max(1, k-1);
			shouldUpdateGramSchmidt = false;
		} else {
			shouldUpdateGramSchmidt = true;
			for (int l = k - 2; l >= 0; l--) {
				red(k,l);
			}
			k = k+1;
		}
	}

	private void swapg(int n) {
		lattice.swapRowsEquals(n,n-1);
		H.swapRowsEquals(n,n-1);

		if (n > 1) {
			for (int j = 0; j <= n - 2; j++) {
				BigFraction temp = mu.get(n, j);
				mu.set(n, j, mu.get(n - 1, j));
				mu.set(n - 1, j, temp);
			}
		}
		BigFraction mutwopointoh = mu.get(n,n-1);
        BigFraction B = sizes[n].add(mutwopointoh.multiply(mutwopointoh).multiply(sizes[n-1]));

		if (sizes[n].equals(BigFraction.ZERO) && mutwopointoh.equals(BigFraction.ZERO)) {
			BigFraction temp = sizes[n];
			sizes[n] = sizes[n-1];
			sizes[n - 1] = temp;
			gramSchmidtBasis.swapRowsEquals(n,n-1);
			for (int i = n+1; i <= kmax; i++) {
				temp = mu.get(i,n);
				mu.set(i,n, mu.get(i,n-1));
				mu.set(i,n-1, temp);
			}
		}
		else if (sizes[n].equals(BigFraction.ZERO)) {
			sizes[n-1] = B;
			gramSchmidtBasis.getRow(n - 1).multiplyEquals(mutwopointoh);
			mu.set(n,n-1, BigFraction.ONE.divide(mutwopointoh));
			for (int i = n + 1; i <= kmax; i++) {
				mu.set(i,n-1, mu.get(i,n-1).divide(mutwopointoh));
			}
		} else {
			BigFraction t = sizes[n-1].divide(B);
			mu.set(n,n-1, mutwopointoh.multiply(t));
			BigVector b = gramSchmidtBasis.getRow(n-1).copy();
			gramSchmidtBasis.setRow(n-1, gramSchmidtBasis.getRow(n).add(b.multiply(mutwopointoh)));
			gramSchmidtBasis.setRow(n, (b.multiply(sizes[k].divide(B))
					.subtract(gramSchmidtBasis.getRow(n).multiply(mu.get(n,n-1)))));
			sizes[n] = sizes[n].multiply(t);
			sizes[n-1] = B;
			for (int i = n+1; i <= kmax; i++) {
				t = mu.get(i,n);
				mu.set(i,n, mu.get(i,n-1).subtract(mutwopointoh.multiply(t)));
				mu.set(i,n-1, t.add(mu.get(n,n-1).multiply(mu.get(i,n))));
			}
		}
	}

	private void red(int n, int l) {
		if (mu.get(n,l).abs().compareTo(eta) <= 0) {
			return;
		}
		BigFraction q =new BigFraction( mu.get(n,l).round() );
		lattice.setRow(n, lattice.getRow(n).subtract(lattice.getRow(l).multiply(q)));
		H.setRow(n, H.getRow(n).subtract(H.getRow(l).multiply(q)));
		mu.set(n,l, mu.get(n,l).subtract(q));
		for (int i = 0; i <= l-1; i++) {
			mu.set(n,i, mu.get(n,i).subtract(mu.get(l,i).multiply(q)));
		}
	}

	public static final class Params {
		protected double delta = 0.75;
		protected boolean debug;

		public Params setDelta(double delta) {
			this.delta = delta;
			return this;
		}

		public Params setDebug(boolean debug) {
			this.debug = debug;
			return this;
		}
	}

	public static final class Result {
		private int numDependantVectors;
		private BigMatrix reducedBasis;
		private BigMatrix transformationsDone;

		private Result(int numDependantVectors, BigMatrix reducedBasis, BigMatrix transformationsDone) {
			this.numDependantVectors = numDependantVectors;
			this.reducedBasis = reducedBasis;
			this.transformationsDone = transformationsDone;
		}

		public int getNumDependantVectors() {
			return numDependantVectors;
		}

		public BigMatrix getReducedBasis() {
			return reducedBasis;
		}

		public BigMatrix getTransformations() {
			return transformationsDone;
		}
	}

}
