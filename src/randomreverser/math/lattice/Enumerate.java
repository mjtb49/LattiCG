package randomreverser.math.lattice;

import com.microsoft.z3.*;
import randomreverser.math.component.Matrix;
import randomreverser.math.component.Vector;
import randomreverser.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Enumerate {

	public static List<Vector> enumerate(int dimensions, Vector lower, Vector upper, Matrix basis, Vector offset) {
		/*System.out.println("Begin enumerate information: ");
		System.out.println(dimensions);
		System.out.println(basis);
		System.out.println(upper);
		System.out.println(lower);
		System.out.println(offset);
		System.out.println("End enumerate information");*/
		Context context = new Context();
		Optimize optimize = context.mkOptimize();
		ArithExpr[] variables = new ArithExpr[dimensions];

		for(int i = 0; i < dimensions; i++) {
			variables[i] = context.mkRealConst("x{"+i+"}");
		}

		BoolExpr[] constraints = new BoolExpr[2 * dimensions];

		for(int i = 0; i < dimensions; i++) {
			int finalI = i;

			Vector face = lower.subtract(offset);
			//System.out.println("Lower Face: "+face);
			Vector normal = new Vector(dimensions, j -> j == finalI ? 1.0D : 0.0D);
			constraints[2 * i] = inHalfPlane(context, variables, basis, face, normal);

			face = upper.subtract(offset);
			//System.out.println("Upper Face: "+face);
			normal = new Vector(dimensions, j -> j == finalI ? -1.0D : 0.0D);
			constraints[2 * i + 1] = inHalfPlane(context, variables, basis, face, normal);
		}
		//for (ArithExpr n : variables)
		//	System.out.println(n);
		Expr[] zeroes = new Expr[dimensions];

		for(int i = 0; i < dimensions; ++i) {
			zeroes[i] = context.mkReal(0);
		}

		//for(BoolExpr c:constraints)
		//System.out.println("THE CONSTRAINTS: "+c);
		optimize.Assert(constraints);

		List<Vector> result = new ArrayList<>();
		solve(context, optimize, variables, new Double[dimensions], result, dimensions - 1);
		return result;
	}

	private static void solve(Context context, Optimize optimize, ArithExpr[] variables, Double[] values, List<Vector> result, int index) {
		//System.out.println(index);
		if(index == -1) {
			result.add(new Vector(variables.length, i -> (double)values[i]));
			//System.out.println(new Vector(variables.length, i -> (double)values[i]));
			return;
		}

		Pair<Long, Long> bounds = getRange(context, optimize, variables[index]);

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

		//System.out.println("Min: " + minHandle);
		long min = (long) Math.ceil(parseDoubleFromZ3Expr(minHandle.getValue())); //TODO: fix this
		optimize.Pop();

		optimize.Push();
		Optimize.Handle maxHandle = optimize.MkMaximize(variable);
		optimize.Check();
		//System.out.println("Max: " + maxHandle);
		long max =(long) Math.floor(parseDoubleFromZ3Expr(maxHandle.getValue())); //TODO: fix this

		optimize.Pop();

		return new Pair<>(min, max);
	}

	private static BoolExpr inHalfPlane(Context context, ArithExpr[] variables, Matrix basis, Vector offset, Vector normal) {
		double rhs = normal.dot(offset);
		Vector lhs = basis.multiply(normal);

		IntNum rhsExpr = context.mkInt((long)rhs);
		ArithExpr[] lhsExprs = new ArithExpr[variables.length];

		for(int i = 0; i < variables.length; ++i) {
			lhsExprs[i] = context.mkMul(variables[i], context.mkInt((long)lhs.get(i)));
		}
		return context.mkGe(context.mkAdd(lhsExprs), rhsExpr);
	}

	private static double parseDoubleFromZ3Expr(Expr expr) {
		//System.out.println("Doing stuff to: "+expr);
		if (expr.isIntNum()) {
			IntNum num = (IntNum) expr;
			//System.out.println("Parsed as: "+(num.getBigInteger().doubleValue()));
			return num.getBigInteger().doubleValue();
		}
			RatNum rational = (RatNum) expr;
			IntNum num = rational.getNumerator();
			IntNum den = rational.getDenominator();
			//System.out.println("Parsed as: "+num.getBigInteger().doubleValue() / den.getBigInteger().doubleValue());
			//TODO is this lossy?
			return  (num.getBigInteger().doubleValue() / den.getBigInteger().doubleValue());
		//}
		//else {
		//	//TODO Bad stuff
		//	System.err.println("Tried to parse non rational expression");
		//	return 0.0;
		//}
	}

}
