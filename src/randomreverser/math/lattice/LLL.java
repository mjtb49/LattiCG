package randomreverser.math.lattice;

import randomreverser.math.component.BigMatrix;
import randomreverser.math.component.BigFraction;
import randomreverser.math.component.BigVector;

public class LLL {

	private static BigMatrix gramSchmidtBasis;
	private static BigMatrix mu;
	private static BigMatrix lattice;
	private static BigMatrix H;
	private static BigFraction[] sizes;
	private static Params params;
	private static int kmax;
	private static int k;
	private static final BigFraction eta = BigFraction.HALF;


    /**
     * LLL lattice reduction implemented as described on page 95 of Henri Cohen's
     * "A course in computational number theory"
     * @param lattice the lattice to reduce
     * @param params the parameters to be passed to LLL
     * @param transformations a matrix in which the transformations done by LLL is stored
     * @return the reduced lattice
     */
	public static BigMatrix reduce(BigMatrix lattice, Params params, BigMatrix transformations) {
		LLL.params = params;
		int n = lattice.getRowCount();
		int m = lattice.getColumnCount();
		gramSchmidtBasis = new BigMatrix(n,m);
		mu = new BigMatrix(n,n);
		k = 1;
		kmax = 0;
		gramSchmidtBasis.setRow(0, lattice.getRow(0).copy());
		//transformations = BigMatrix.identityMatrix(n);
		H = transformations;
		LLL.lattice = lattice.copy();
		sizes = new BigFraction[n];
		sizes[0] = LLL.lattice.getRow(0).magnitudeSq();

		while (k < n) {
			if (k > kmax) {
				kmax = k;
				incGramSchmidt();
			}
			testCondition();
		}
		return LLL.lattice;
	}

	private static void incGramSchmidt() {
		for (int j = 0; j <= k - 1; j++) {
			//System.out.println(k+" "+j);
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
        //System.out.println(gramSchmidtBasis.toPrettyString());
		sizes[k] = newRow.magnitudeSq();
	}

	private static void testCondition() {
		red(k,k-1);
		/*System.out.println("BEGIN INFO");
		System.out.println(mu + "\n");
		System.out.println(gramSchmidtBasis + "\n");
		System.out.println(lattice + "\n");*/
		//System.out.println(Arrays.toString(sizes));
		if (sizes[k].toDouble() < ((params.delta - (mu.get(k,k-1).multiply(mu.get(k,k-1))).toDouble())* (sizes[k-1]).toDouble())) { //TODO I don't trust this comparison as doubles
		    swapg(k);
			k = Math.max(1, k-1);
			//testCondition(); //TODO is this needed / a good way to do this? I don't understand the pseudocode here
		} else {
			for (int l = k - 2; l >= 0; l--) {
				red(k,l);
			}
			k = k+1;
		}
	}

	private static void swapg(int n) {
		/*System.out.println(lattice.toPrettyString());
		System.out.println(mu.toPrettyString());
		System.out.println(gramSchmidtBasis.toPrettyString()); */
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
			//System.out.println("A");
			BigFraction temp = sizes[n];
			sizes[n] = sizes[n-1];
			sizes[n - 1] = temp;
			gramSchmidtBasis.swapRowsEquals(n,n-1);
			for (int i = n+1; i <= kmax; i++) {
				//mu.getRow(i).swapEquals(n,n-1);
				temp = mu.get(i,n);
				mu.set(i,n, mu.get(i,n-1));
				mu.set(i,n-1, temp);
			}
		}
		else if (sizes[n].equals(BigFraction.ZERO)) { /*&& mutwopointoh.compareTo(epsilon) <= 0 &&  mutwopointoh.compareTo(epsilon.multiply(BigDecimal.valueOf(-1))) >= 0*/
			//System.out.println(mutwopointoh);
			//System.out.println("B");
			sizes[n-1] = B;
			//System.out.println(gramSchmidtBasis.getRow(n-1));
			gramSchmidtBasis.getRow(n - 1).multiplyEquals(mutwopointoh);
			//System.out.println(gramSchmidtBasis.getRow(n-1));
			mu.set(n,n-1, BigFraction.ONE.divide(mutwopointoh));
			for (int i = n + 1; i <= kmax; i++) {
				mu.set(i,n-1, mu.get(i,n-1).divide(mutwopointoh));
			}
		} else {
			//System.out.println("C");
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

	private static void red(int n, int l) {
		if (mu.get(n,l).abs().compareTo(eta) <= 0) {
			return;
		}
		BigFraction q =new BigFraction( mu.get(n,l).round() );
		//System.out.println("Before:\n"+lattice.toPrettyString());
		//System.out.println(mu);
		//System.out.println(gramSchmidtBasis);
		lattice.setRow(n, lattice.getRow(n).subtract(lattice.getRow(l).multiply(q)));
		//System.out.println("After:\n"+lattice.toPrettyString());
		H.setRow(n, H.getRow(n).subtract(H.getRow(l).multiply(q)));
		mu.set(n,l, mu.get(n,l).subtract(q));
		for (int i = 0; i <= l-1; i++) {
			mu.set(n,i, mu.get(n,i).subtract(mu.get(l,i).multiply(q)));
		}
	}

	public static final class Params {
		protected double delta;
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

}
