package randomreverser.math.decomposition;

import randomreverser.math.component.Matrix;
import randomreverser.math.component.Vector;

public class LUDecomposition {

	public static LUResult decompose(Matrix matrix) {
		if(!matrix.isSquare()) {
			throw new IllegalArgumentException("Matrix is not square");
		}

		Matrix m = matrix.copy();
		int size = m.getRowCount();
		Vector p = new Vector(size);
		int swaps = 0;

		for(int i = 0; i < size; i++) {
			int pivot = -1;
			double beegestNumbor = 0.0D;

			for(int row = i; row < size; row++) {
				double d = Math.abs(m.get(row, i));

				if(d > beegestNumbor) {
					beegestNumbor = d;
					pivot = row;
				}
			}

			if(pivot == -1) {
				throw new IllegalStateException("Matrix is singular");
			}

			p.set(i, pivot);

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

		//Inverse
		Matrix inv = m.copy();

		for(int row = 0; row < size; row++) {
			if(row == (int)p.get(row))continue;
			inv.swapRowsEquals(row, (int)p.get(row));
		}

		for(int dcol = 0; dcol < size; dcol++) {
			for(int row = 0; row < size; row++) {
				for(int col = 0; col < row; col++) {
					inv.set(row, dcol, inv.get(row, dcol) - m.get(row, col) * inv.get(col, dcol));
				}
			}
		}

		for(int dcol = 0; dcol < size; dcol++) {
			for(int row = size - 1; row >= 0; row--) {
				for(int col = size - 1; col > row; col--) {
					inv.set(row, dcol, inv.get(row, dcol) - m.get(row, col) * inv.get(col, dcol));
				}

				inv.set(row, dcol, inv.get(row, dcol) / m.get(row, row));
			}
		}

		return new LUResult(m, p, det, inv);
	}

}
