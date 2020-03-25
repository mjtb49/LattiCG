package seedutils.math;

import seedutils.magic.RandomReverser;
import seedutils.math.component.BigMatrix;
import seedutils.math.component.Matrix;
import seedutils.math.component.Vector;
import seedutils.math.lattice.LLL;

public class Test {

	public static void main(String[] args) {
		Matrix matrix = new Matrix.Builder().setSize(3, 3).fillWith(0.0D).build();
		matrix.setRow(0, new Vector(new double[] {1.0D, 5.0D, 3.0D}));
		matrix.setRow(1, new Vector(new double[] {4.0D, -2.0D, 9.0D}));
		matrix.setRow(2, new Vector(new double[] {-7.0D, 1.0D, 2.0D}));
		System.out.println(matrix);

		BigMatrix basis = new BigMatrix.Factory().fromString("{{1, 103, 107}, {0, 200, 0}, {0, 0, 200}}");
		LLL.Params params = new LLL.Params().setDelta(0.75).setDebug(false);

		BigMatrix reducedBasis = LLL.reduce(basis, params);

		System.out.println("Basis: " + basis);
		System.out.println("Reduced basis: " + reducedBasis);

		BigMatrix m1 = new BigMatrix.Factory().fromString("{{1, 103, 107}, {0, 200, 0}, {0, 0, 200}}");
		BigMatrix m2 = new BigMatrix.Factory().fromString("{{1, 103, 107}, {0, 200, 0}, {0, 0, 200}}");
		System.out.println(m1.multiply(m2));

		Matrix m3 = new Matrix.Factory().fromBigMatrix(m1);
		Matrix m4 = new Matrix.Factory().fromBigMatrix(m2);
		System.out.println(m3.multiply(m4));

		RandomReverser reverser = new RandomReverser();
		reverser.addNextIntCall(16,12,13);
		reverser.addNextIntCall(128,12,13);
		reverser.addNextIntCall(16,12,13);
		reverser.addNextIntCall(4,2,3);
		reverser.addNextIntCall(4,2,3);
		reverser.addNextIntCall(4,2,3);
		reverser.addNextIntCall(4,2,3);
		reverser.addNextIntCall(4,2,3);
		reverser.addNextIntCall(4,2,3);
		reverser.addNextIntCall(4,2,3);

		double s = System.currentTimeMillis();
		reverser.findAllValidSeeds();
		System.out.println(System.currentTimeMillis() - s);
	}

}
