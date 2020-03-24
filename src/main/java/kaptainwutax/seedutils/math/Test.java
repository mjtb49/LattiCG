package kaptainwutax.seedutils.math;

import kaptainwutax.seedutils.math.component.BigMatrix;
import kaptainwutax.seedutils.math.component.Matrix;
import kaptainwutax.seedutils.math.component.Vector;
import kaptainwutax.seedutils.math.lattice.LLL;

public class Test {

	public static void main(String[] args) {
		Matrix matrix = new Matrix.Builder().setSize(3, 3).fillWith(0.0D).build();
		matrix.setRow(0, new Vector(new double[] {1.0D, 5.0D, 3.0D}));
		matrix.setRow(1, new Vector(new double[] {4.0D, -2.0D, 9.0D}));
		matrix.setRow(2, new Vector(new double[] {-7.0D, 1.0D, 2.0D}));
		System.out.println(matrix);

		BigMatrix basis = new BigMatrix.Factory().fromString("{{1, 103, 107}, {0, 200, 0}, {0, 0, 200}}");
		LLL.Params params = new LLL.Params().setDelta(1.0D).setDebug(false);

		BigMatrix reducedBasis = LLL.reduce(basis, params);

		System.out.println("Basis: " + basis);
		System.out.println("Reduced basis: " + reducedBasis);

		BigMatrix m1 = new BigMatrix.Factory().fromString("{{1, 103, 107}, {0, 200, 0}, {0, 0, 200}}");
		BigMatrix m2 = new BigMatrix.Factory().fromString("{{1, 103, 107}, {0, 200, 0}, {0, 0, 200}}");
		System.out.println(m1.multiply(m2));

		Matrix m3 = new Matrix.Factory().fromBigMatrix(m1);
		Matrix m4 = new Matrix.Factory().fromBigMatrix(m2);
		System.out.println(m3.multiply(m4));
	}

}
