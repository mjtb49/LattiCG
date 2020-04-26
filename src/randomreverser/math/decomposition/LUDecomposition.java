package randomreverser.math.decomposition;

import randomreverser.math.component.Matrix;

public class LUDecomposition {

	public static void main(String[] args) {
		Matrix m = Matrix.fromString("{{1, 5}, {2, -3}}");
		LUResult result = LUDecomposition.decompose(m);
		System.out.println(m.toPrettyString() + "\n");
		System.out.println(result.toPrettyString() + "\n");
		System.out.println(result.getL().multiply(result.getU()).toPrettyString() + "\n");
		System.out.println(result.getDet());
	}

	public static LUResult decompose(Matrix matrix) {
		if(!matrix.isSquare()) {
			throw new IllegalArgumentException("Matrix is not square");
		}

		Matrix m = matrix.copy();
		int size = m.getRowCount();

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

		//Determinant
		double det = 1.0D;

		for(int i = 0; i < size; i++) {
			det *= m.get(i, i);
		}

		return new LUResult(m, det);
	}

}
