package randomreverser.math.lattice;

import randomreverser.math.component.BigFraction;
import randomreverser.math.component.BigMatrix;
import randomreverser.math.component.Matrix;
import randomreverser.math.decomposition.BigGramSchmidt;
import randomreverser.math.decomposition.GramSchmidt;

public class LLL {

	public static Matrix reduce(Matrix m, Params params) {
		if(params.delta <= 0.25D && params.delta > 1.0D) {
			throw new IllegalArgumentException("Delta must be in the range of (0.25, 1]");
		}

		GramSchmidt gs = new GramSchmidt(m.copy());
		gs.compute();

		int k = 1;
		int n = gs.getBasis().getRowCount();
		while(k <= n) {
			//for(int k = 0; k < gs.getBasis().getHeight(); k++) {
			for(int j = k - 1; j >= 0; j--) {
				long nearestLong = Math.round(gs.getCoefficients().get(k, j));
				if (nearestLong != 0){
					gs.getBasis().getRow(k).subtractEquals(gs.getBasis().getRow(j).multiply(nearestLong));
					gs.compute(); //bad and naive
				}
			}
			//}
			double v = gs.getNewBasis().getRow(k).magnitudeSq() + (gs.getNewBasis().getRow(k-1).multiply(gs.getCoefficients().get(k, k -1 ))).magnitudeSq();
			if(v < params.delta * gs.getNewBasis().getRow(k-1).magnitudeSq()) {
				gs.getBasis().swapRowsEquals(k - 1, k );
				gs.compute(); //bad and naive
				k = (k >= 2) ? k - 1: 1;
			}
			else {
				k += 1;
			}

		}

		return gs.getBasis();
	}

	public static BigMatrix reduce(BigMatrix m, Params params) {
		return reduce( m,  params, null);
	}

	public static BigMatrix reduce(BigMatrix m, Params params, BigMatrix transformations) {
		if(params.delta <= 0.25D && params.delta > 1.0D) {
			throw new IllegalArgumentException("Delta must be in the range of (0.25, 1]");
		}

		BigGramSchmidt gs = new BigGramSchmidt(m.copy());
		gs.compute();

		int k = 1;
		int n = gs.getBasis().getRowCount() - 1;
		while(k <= n) {
			//for(int k = 0; k < gs.getBasis().getHeight(); k++) {
			for(int j = k - 1; j >= 0; j--) {
				BigFraction rounded = new BigFraction(gs.getCoefficients().get(k, j).round());
				if (rounded.signum() != 0) {
					gs.getBasis().getRow(k).subtractEquals(gs.getBasis().getRow(j).multiply(rounded));
					if (transformations != null)
						transformations.getRow(k).subtractEquals(transformations.getRow(j).multiply(rounded));
					gs.compute(); //bad and naive
				}
			}
			//}

			//boolean fullyCompleted = true;

			//for(int k = 0; k < gs.getBasis().getHeight() - 1; k++) {
				//BigVector v = gs.getNewBasis().getRow(k).add(gs.getNewBasis().getRow(k-1).scale(gs.getCoefficients().get(k-1, k)));
				BigFraction a = gs.getNewBasis().getRow(k).magnitudeSq().add(gs.getNewBasis().getRow(k-1).multiply(gs.getCoefficients().get(k, k-1)).magnitudeSq());

				if(a.toDouble() < gs.getNewBasis().getRow(k-1).magnitudeSq().toDouble() * params.delta) {
					gs.getBasis().swapRowsEquals(k-1, k);
					if (transformations != null)
						transformations.swapRowsEquals(k-1, k);
					gs.compute(); //bad and naive
					k = (k >= 2) ? k - 1: 1;
					//fullyCompleted = false;
					//break;
				} else {
					k += 1;
				}
			//}

			//if(fullyCompleted)break;
		}

		return gs.getBasis();
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
