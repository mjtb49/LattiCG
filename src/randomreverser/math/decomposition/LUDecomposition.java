package randomreverser.math.decomposition;

import randomreverser.math.component.Matrix;

public class LUDecomposition {

	public static LUResult decompose(Matrix matrix) {
		if(!matrix.isSquare()) {
			throw new IllegalArgumentException("Matrix is not square");
		}

		Matrix m = matrix.copy();
		int size = m.getRowCount();
		Matrix p = Matrix.identityMatrix(size);
		int swaps = 0;

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

			p.swapRowsEquals(i, pivot);

			if(pivot != i) {
				m.swapRowsEquals(i, pivot);
				swaps++;
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

		det *= (swaps & 1) == 0 ? 1 : -1;
		return new LUResult(m, p, det);
	}

}
