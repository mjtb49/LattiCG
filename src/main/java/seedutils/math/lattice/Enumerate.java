package seedutils.math.lattice;

import com.microsoft.z3.*;
import seedutils.math.component.Matrix;
import seedutils.math.component.Vector;
import seedutils.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Enumerate {

	public static List<Vector> enumerate(int dimensions, Vector lower, Vector upper, Matrix basis, Vector offset) {
		Context context = new Context();
		Optimize optimize = context.mkOptimize();
		ArithExpr[] variables = new ArithExpr[dimensions];

		for(int i = 0; i < dimensions; i++) {
			variables[i] = context.mkRealConst("x{i}");
		}

		BoolExpr[] constraints = new BoolExpr[2 * dimensions];

		for(int i = 0; i < dimensions; i++) {
			int finalI = i;

			Vector face = lower.subtract(offset);
			Vector normal = new Vector(dimensions, j -> j == finalI ? 1.0D : 0.0D);
			constraints[2 * i] = inHalfPlane(context, variables, basis, face, normal);

			face = upper.subtract(offset);
			normal = new Vector(dimensions, j -> j == finalI ? -1.0D : 0.0D);
			constraints[2 * i + 1] = inHalfPlane(context, variables, basis, face, normal);
		}

		Expr[] zeroes = new Expr[dimensions];

		for(int i = 0; i < dimensions; ++i) {
			zeroes[i] = context.mkReal(0);
		}

		optimize.Assert(constraints);

		List<Vector> result = new ArrayList<>();
		solve(context, optimize, variables, new Double[dimensions], result, dimensions - 1);
		return result;
	}

	private static void solve(Context context, Optimize optimize, ArithExpr[] variables, Double[] values, List<Vector> result, int index) {
		if(index == -1) {
			result.add(new Vector(variables.length, i -> (double)values[i]));
			return;
		}

		Pair<Long, Long> bounds = getRange(context, optimize, variables[index]);

		//var (min, max) = GetRange(context, optimize, variables[index]);

		for(double x = bounds.getFirst(); x <= bounds.getSecond(); x++) {
			values[index] = x;
			optimize.Push();
			optimize.Assert(context.mkEq(variables[index], context.mkInt((long)x)));
			solve(context, optimize, variables, values, result, index - 1);
			optimize.Pop();
		}

		values[index] = null;
	}

	private static Pair<Long, Long> getRange(Context context, Optimize optimize, ArithExpr variable) {
		optimize.Push();
		Optimize.Handle minHandle = optimize.MkMinimize(variable);
		optimize.Check();

		long min = Long.parseLong(minHandle.getValue().toString()); //TODO: fix this
		optimize.Pop();

		optimize.Push();
		Optimize.Handle maxHandle = optimize.MkMaximize(variable);
		optimize.Check();
		long max = Long.parseLong(maxHandle.getValue().toString()); //TODO: fix this

		optimize.Pop();

		return new Pair<>(min, max);
	}

	private static BoolExpr inHalfPlane(Context context, ArithExpr[] variables, Matrix basis, Vector offset, Vector normal) {
		double rhs = normal.dot(offset);
		Vector lhs = normal.multiply(basis);

		IntNum rhsExpr = context.mkInt((long)rhs);
		ArithExpr[] lhsExprs = new ArithExpr[variables.length];

		for(int i = 0; i < variables.length; ++i) {
			lhsExprs[i] = context.mkMul(variables[i], context.mkInt((long)lhs.get(i)));
		}

		return context.mkGe(context.mkAdd(lhsExprs), rhsExpr);
	}

}
