package seedutils.math;

import seedutils.math.component.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SystemSolver {

	public static SystemSolver.Result solve(Matrix base, Matrix extra, Phase phase) {
		AugmentedMatrix am = new AugmentedMatrix(base.copy(), extra.copy());

		for(int x = 0; x < am.getBase().getWidth(); x++) {
			Vector v = am.getBase().getRow(x);

			if(v.get(x) != 0.0D && v.get(x) != 1.0D) {
				am.multiplyRow(x, 1.0D / v.get(x));
			} else if(v.get(x) == 0.0D) {
				continue;
			}

			for(int y = x + 1; y < am.getBase().getHeight(); y++) {
				if(am.getBase().get(y, x) == 0.0D)continue;
				am.subtractScaledRow(y, am.getBase().get(y, x), x);
			}
		}

		if(phase == Phase.ROW_ECHELON)return new Result(am);

		for(int j = 0; j < am.getBase().getWidth(); j++) {
			for(int i = 0; i < j; i++) {
				Vector v = am.getBase().getRow(i);
				Vector s = am.getBase().getRow(j);
				if(v.get(j) == 0.0D || s.get(j) != 1.0D)continue;
				am.subtractScaledRow(i, v.get(j), j);
			}
		}

		return new Result(am);
	}

	public static SystemSolver.BigResult solve(BigMatrix base, BigMatrix extra, Phase phase) {
		BigAugmentedMatrix am = new BigAugmentedMatrix(base.copy(), extra.copy());

		for(int x = 0; x < am.getBase().getWidth(); x++) {
			BigVector v = am.getBase().getRow(x);

			if(v.get(x).compareTo(BigDecimal.ZERO) != 0 && v.get(x).compareTo(BigDecimal.ONE) != 0) {
				am.multiplyRow(x, BigDecimal.ONE.setScale(v.get(x).scale(), RoundingMode.HALF_UP).divide(v.get(x), RoundingMode.HALF_UP));
			} else if(v.get(x).compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			for(int y = x + 1; y < am.getBase().getHeight(); y++) {
				if(am.getBase().get(y, x).compareTo(BigDecimal.ZERO) == 0)continue;
				am.subtractScaledRow(y, am.getBase().get(y, x), x);
			}
		}

		if(phase == Phase.ROW_ECHELON)return new BigResult(am);

		for(int j = 0; j < am.getBase().getWidth(); j++) {
			for(int i = 0; i < j; i++) {
				BigVector v = am.getBase().getRow(i);
				BigVector s = am.getBase().getRow(j);
				if(v.get(j).compareTo(BigDecimal.ZERO) == 0 || s.get(j).compareTo(BigDecimal.ONE) != 0)continue;
				am.subtractScaledRow(i, v.get(j), j);
			}
		}

		return new BigResult(am);
	}

	public enum Phase {
		ROW_ECHELON, BASIS
	}

	public static class Result {
		public Matrix result;
		public AugmentedMatrix remainder;
		public Type type;

		public Result(AugmentedMatrix am) {
			this.result = new Matrix(am.getExtra().getHeight(), am.getExtra().getWidth());
			this.remainder = am;
			this.type = Type.ONE_SOLUTION;

			for(int i = 0; i < am.getBase().getHeight(); i++) {
				Vector baseV = am.getBase().getRow(i);
				Vector extraV = am.getExtra().getRow(i);
				boolean isBaseZero = baseV.isZero();
				boolean isExtraZero = extraV.isZero();

				if(!isBaseZero) {
					//TODO: It may not always yield a solution.
					this.result.setRow(i, extraV);
					this.updateType(Type.ONE_SOLUTION);
				} else if(isExtraZero) {
					this.remainder.nullifyRow(i);
					this.updateType(Type.INFINITE_SOLUTIONS);
				} else {
					this.updateType(Type.NO_SOLUTIONS);
				}
			}
		}

		public void updateType(Type type) {
			if(type.ordinal() > this.type.ordinal()) {
				this.type = type;
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("This system has ").append(this.type).append(".\n\n");
			sb.append("Result: \n").append(this.result.toPrettyString()).append("\n\n");
			sb.append("Remainder: \n").append(this.remainder.toString());
			return sb.toString();
		}

		public enum Type {
			ONE_SOLUTION, NO_SOLUTIONS, INFINITE_SOLUTIONS
		}
	}

	public static class BigResult {
		public BigMatrix result;
		public BigAugmentedMatrix remainder;
		public Type type;

		public BigResult(BigAugmentedMatrix am) {
			this.result = new BigMatrix(am.getExtra().getHeight(), am.getExtra().getWidth());
			this.remainder = am;
			this.type = Type.ONE_SOLUTION;

			for(int i = 0; i < am.getBase().getHeight(); i++) {
				BigVector baseV = am.getBase().getRow(i);
				BigVector extraV = am.getExtra().getRow(i);
				boolean isBaseZero = baseV.isZero();
				boolean isExtraZero = extraV.isZero();

				if(!isBaseZero) {
					//TODO: It may not always yield a solution.
					this.result.setRow(i, extraV);
					this.updateType(Type.ONE_SOLUTION);
				} else if(isExtraZero) {
					this.remainder.nullifyRow(i);
					this.updateType(Type.INFINITE_SOLUTIONS);
				} else {
					this.updateType(Type.NO_SOLUTIONS);
				}
			}
		}

		public void updateType(Type type) {
			if(type.ordinal() > this.type.ordinal()) {
				this.type = type;
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("This system has ").append(this.type).append(".\n\n");
			sb.append("Result: \n").append(this.result.toPrettyString()).append("\n\n");
			sb.append("Remainder: \n").append(this.remainder.toString());
			return sb.toString();
		}

		public enum Type {
			ONE_SOLUTION, NO_SOLUTIONS, INFINITE_SOLUTIONS
		}
	}

}
