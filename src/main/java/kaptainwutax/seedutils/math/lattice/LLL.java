package kaptainwutax.seedutils.math.lattice;

import kaptainwutax.seedutils.math.component.BigMatrix;
import kaptainwutax.seedutils.math.component.Matrix;
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

		if(params.debug) {
			System.out.format("Initial basis: %s\n", gs.getBasis());
			System.out.format("Initial GS basis: %s\n", gs.getNewBasis());
			System.out.format("Initial GS coefficients: %s\n\n", gs.getCoefficients());
		}

		for(int k = 1; k < gs.getBasis().getHeight(); ) {
			if(params.debug)System.out.format("Iteration [%d] ===================\n", k);

			for(int j = k - 1; j >= 0; j--) {
				if(params.debug)System.out.format(" -> Iteration [%d] =====\n", j);

				if(gs.getCoefficients().get(k, j) > 0.5D) {
					if(params.debug)System.out.format(" -> Is bigger than 1 / 2\n");

					gs.getBasis().getRow(k).subtractEquals(
							gs.getBasis().getRow(j).scale(Math.round(gs.getCoefficients().get(k, j)))
					);

					gs.compute(); //bad and naive

					if(params.debug) {
						System.out.format("    -> New basis: %s\n", gs.getBasis());
						System.out.format("    -> New GS basis: %s\n", gs.getNewBasis());
						System.out.format("    -> New GS coefficients: %s\n", gs.getCoefficients());
					}
				}
			}

			double c = gs.getCoefficients().get(k, k - 1);
			c = c * c;

			if(gs.getNewBasis().getRow(k).magnitudeSq() >=
					(params.delta - c) * gs.getNewBasis().getRow(k - 1).magnitudeSq()) {
				k++;
			} else {
				System.out.format(" -> Swapped %d and %d: was %s, is %s\n", k, k - 1, gs.getBasis(), gs.getBasis().swap(k, k - 1));
				gs.getBasis().swapEquals(k, k - 1);
				gs.compute(); //bad and naive
				k = Math.max(k - 1, 1);
			}
		}

		return gs.getBasis();
	}

	public static BigMatrix reduce(BigMatrix m, Params params) {
		if(params.delta <= 0.25D && params.delta > 1.0D) {
			throw new InvalidParameterException("Delta must be in the range of (0.25, 1]");
		}

		BigDecimal HALF = BigDecimal.valueOf(0.5D);
		BigDecimal BIG_DELTA = BigDecimal.valueOf(params.delta);

		BigGramSchmidt gs = new BigGramSchmidt(m.copy());
		gs.compute();

		if(params.debug) {
			System.out.format("Initial basis: %s\n", gs.getBasis());
			System.out.format("Initial GS basis: %s\n", gs.getNewBasis());
			System.out.format("Initial GS coefficients: %s\n\n", gs.getCoefficients());
		}

		for(int k = 1; k < gs.getBasis().getHeight(); ) {
			if(params.debug)System.out.format("Iteration [%d] ===================\n", k);

			for(int j = k - 1; j >= 0; j--) {
				if(params.debug)System.out.format(" -> Iteration [%d] =====\n", j);

				if(gs.getCoefficients().get(k, j).compareTo(HALF) > 0) {
					if(params.debug)System.out.format(" -> Is bigger than 1 / 2\n");

					gs.getBasis().getRow(k).subtractEquals(
							gs.getBasis().getRow(j).scale(gs.getCoefficients().get(k, j).setScale(0, RoundingMode.HALF_UP))
					);

					gs.compute(); //bad and naive

					if(params.debug) {
						System.out.format("    -> New basis: %s\n", gs.getBasis());
						System.out.format("    -> New GS basis: %s\n", gs.getNewBasis());
						System.out.format("    -> New GS coefficients: %s\n", gs.getCoefficients());
					}
				}
			}

			BigDecimal c = gs.getCoefficients().get(k, k - 1);
			c = c.multiply(c);

			if(gs.getNewBasis().getRow(k).magnitudeSq()
					.compareTo(BIG_DELTA.subtract(c).multiply(gs.getNewBasis().getRow(k - 1).magnitudeSq())) >= 0) {
				k++;
			} else {
				gs.getBasis().swapEquals(k, k - 1);
				gs.compute(); //bad and naive
				k = Math.max(k - 1, 1);
			}
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
