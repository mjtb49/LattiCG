package randomreverser.math.decomposition;

import randomreverser.math.component.Matrix;
import randomreverser.math.component.Vector;

public class SolverThingsINeedABetterName {

	public static void main(String[] args) {
		Matrix m = Matrix.fromString("{{4,3,2,1},{1,10,3,4},{5,3,2,-4},{4,8,7,9}}");
		LUResult result = SolverThingsINeedABetterName.luDecompose(m);
		System.out.println(m.toPrettyString());
		System.out.println(result.toPrettyString());
		System.out.println(result.getL().multiply(result.getU()).toPrettyString());
	}

	public static LUResult luDecompose(Matrix matrix) {
		if(!matrix.isSquare()) {
			throw new IllegalArgumentException("Matrix is not square");
		}

		Matrix m = matrix.copy();
		int size = m.getRowCount();
		Vector p = new Vector(size);

		for(int i = 0; i < size; i++) {
			int pivot = -1;

			for(int row = i; row < size; row++) {
				if(m.get(row, i) != 0) {
					pivot = row;
					break;
				}
			}

			if(pivot == -1) {
				throw new IllegalStateException("Matrix is singular");
			}

			p.set(i, pivot);

			if(pivot != i) {
				m.swapRowsEquals(i, pivot);
			}

			for(int row = i + 1; row < size; row++) {
				m.set(row, i, m.get(row, i) / m.get(i, i));
			}

			for(int row = i + 1; row < size; row++) {
				for(int col = i + 1; col < size; col++) {
					m.set(row, col, m.get(row, col) - m.get(row, i) * m.get(i, col));
				}
			}
		}

		return new LUResult(m, p);
	}

	public static double getDeterminant(LUResult result) {
		return 0.0D;
	}

}
