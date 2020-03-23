package kaptainwutax.seedutils.math.lattice;

import kaptainwutax.seedutils.math.component.Basis;
import kaptainwutax.seedutils.math.component.Matrix;
import kaptainwutax.seedutils.math.component.number.NumberType;
import kaptainwutax.seedutils.math.decomposition.GramSchmidt;

import java.math.BigDecimal;
import java.security.InvalidParameterException;

public class LLL<T extends NumberType<?, T>> extends LatticeReduction<T, LLL.Parameters<T>> {

	@Override
	public Basis<T> reduce(Basis<T> b, Parameters<T> params) {
		if(params.delta.getRaw().compareTo(BigDecimal.valueOf(0.25D)) <= 0
				|| params.delta.getRaw().compareTo(BigDecimal.valueOf(1.0D)) > 0) {
			throw new InvalidParameterException("Delta must be in the range of (0.25, 1]");
		}

		b = b.copy();

		GramSchmidt<T> gramSchmidt = b.getGramSchmidt();
		Basis<T> q = gramSchmidt.getBasis();
		Matrix<T> u = gramSchmidt.getCoefficients();

		if(params.debug) {
			System.out.format("Initial basis: %s\n", b);
			System.out.format("Initial GS basis: %s\n", q);
			System.out.format("Initial GS coefficients: %s\n\n", u);
		}

		for(int k = 1; k < b.getLength(); ) {
			if(params.debug)System.out.format("Iteration [%d] ===================\n", k);

			for(int j = k - 1; j >= 0; j--) {
				if(params.debug)System.out.format(" -> Iteration [%d] =====\n", j);

				if(u.get(k, j).getRaw().compareTo(BigDecimal.valueOf(0.5D)) > 0) {
					if(params.debug)System.out.format(" -> Is bigger than 1 / 2\n");

					b.getVector(k).subtractEquals(b.getVector(j).scale(u.get(k, j).round()));

					//bad and naive
					gramSchmidt = b.getGramSchmidt();
					q = gramSchmidt.getBasis();
					u = gramSchmidt.getCoefficients();

					if(params.debug) {
						System.out.format("    -> New basis: %s\n", b);
						System.out.format("    -> New GS basis: %s\n", q);
						System.out.format("    -> New GS coefficients: %s\n", u);
					}
				}
			}

			T c = u.get(k, k - 1);
			c.multiplyEquals(c);

			T sub = params.delta.subtract(c).multiply(q.getVector(k - 1).getMagnitudeSq());

			if(q.getVector(k).getMagnitudeSq().getRaw().compareTo(sub.getRaw()) >= 0) {
				k++;
			} else {
				b.swap(k, k - 1);

				//bad and naive
				gramSchmidt = b.getGramSchmidt();
				q = gramSchmidt.getBasis();
				u = gramSchmidt.getCoefficients();
				k = Math.max(k - 1, 1);
			}
		}

		return b;
	}

	public static class Parameters<T> {
		protected T delta;
		protected boolean debug;

		public Parameters() {
		}

		public Parameters<T> setDelta(T delta) {
			this.delta = delta;
			return this;
		}

		public Parameters<T> setDebug(boolean debug) {
			this.debug = debug;
			return this;
		}
	}

}
