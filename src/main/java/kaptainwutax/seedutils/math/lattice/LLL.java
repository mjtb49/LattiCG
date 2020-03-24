package kaptainwutax.seedutils.math.lattice;

import kaptainwutax.seedutils.math.component.BigMatrix;
import kaptainwutax.seedutils.math.component.BigVector;
import kaptainwutax.seedutils.math.component.Matrix;
import kaptainwutax.seedutils.math.component.Vector;
import kaptainwutax.seedutils.math.decomposition.BigGramSchmidt;
import kaptainwutax.seedutils.math.decomposition.GramSchmidt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidParameterException;

public class LLL {

	public static Matrix reduce(Matrix m, Params params) {
		if(params.delta <= 0.25D && params.delta > 1.0D) {
			throw new InvalidParameterException("Delta must be in the range of (0.25, 1]");
		}

		GramSchmidt gs = new GramSchmidt(m.copy());
		gs.compute();

		while(true) {
			for(int k = 0; k < gs.getBasis().getHeight(); k++) {
				for(int j = k - 1; j >= 0; j--) {
					long nearestLong = Math.round(gs.getCoefficients().get(k, j));
					gs.getBasis().getRow(k).subtractEquals(gs.getBasis().getRow(j).scale(nearestLong));
					gs.compute(); //bad and naive
				}
			}

			boolean fullyCompleted = true;

			for(int k = 0; k < gs.getBasis().getHeight() - 1; k++) {
				Vector v = gs.getNewBasis().getRow(k + 1).add(gs.getNewBasis().getRow(k).scale(gs.getCoefficients().get(k, k + 1)));
				if(v.magnitudeSq() < params.delta * gs.getNewBasis().getRow(k).magnitudeSq()) {
					gs.getBasis().swapEquals(k, k + 1);
					gs.compute(); //bad and naive
					fullyCompleted = false;
					break;
				}
			}

			if(fullyCompleted)break;
		}

		return gs.getBasis();
	}

	public static BigMatrix reduce(BigMatrix m, Params params) {
		if(params.delta <= 0.25D && params.delta > 1.0D) {
			throw new InvalidParameterException("Delta must be in the range of (0.25, 1]");
		}

		BigDecimal BIG_DELTA = BigDecimal.valueOf(params.delta);

		BigGramSchmidt gs = new BigGramSchmidt(m.copy());
		gs.compute();

		while(true) {
			for(int k = 0; k < gs.getBasis().getHeight(); k++) {
				for(int j = k - 1; j >= 0; j--) {
					BigDecimal rounded = gs.getCoefficients().get(k, j).setScale(0, RoundingMode.HALF_UP);
					gs.getBasis().getRow(k).subtractEquals(gs.getBasis().getRow(j).scale(rounded));
					gs.compute(); //bad and naive
				}
			}

			boolean fullyCompleted = true;

			for(int k = 0; k < gs.getBasis().getHeight() - 1; k++) {
				BigVector v = gs.getNewBasis().getRow(k + 1).add(gs.getNewBasis().getRow(k).scale(gs.getCoefficients().get(k, k + 1)));

				if(v.magnitudeSq().compareTo(gs.getNewBasis().getRow(k).magnitudeSq().multiply(BIG_DELTA)) < 0) {
					gs.getBasis().swapEquals(k, k + 1);
					gs.compute(); //bad and naive
					fullyCompleted = false;
					break;
				}
			}

			if(fullyCompleted)break;
		}

		return gs.getBasis();
	}

	public static class Params {
		protected double delta;
		protected boolean debug;

		public Params() {
		}

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
