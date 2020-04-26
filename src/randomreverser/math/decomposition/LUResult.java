package randomreverser.math.decomposition;

import randomreverser.math.component.Matrix;

import java.util.regex.Pattern;

public class LUResult {

	protected final int size;
	protected final Matrix L;
	protected final Matrix U;
	private final double det;

	public LUResult(Matrix lu, double det) {
		this.size = lu.getRowCount();

		this.L = new Matrix(this.size, this.size, (row, col) -> {
			if(row > col)return lu.get(row, col);
			else if(row == col)return 1.0D;
			else return 0.0D;
		});

		this.U = new Matrix(this.size, this.size, (row, col) -> {
			if(row <= col)return lu.get(row, col);
			else return 0.0D;
		});

		this.det = det;
	}

	public int getMatrixSize() {
		return this.size;
	}

	public Matrix getL() {
		return this.L;
	}

	public Matrix getU() {
		return this.U;
	}

	public double getDet() {
		return this.det;
	}

	public String toPrettyString() {
		StringBuilder sb = new StringBuilder();
		String[] uStuff = this.U.toPrettyString().split(Pattern.quote("\n"));
		String[] lStuff = this.L.toPrettyString().split(Pattern.quote("\n"));

		for(int i = 0; i < lStuff.length; i++) {
			sb.append(lStuff[i]).append("  ").append(uStuff[i]).append("  ");
			if(i != lStuff.length - 1)sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return this.L + " | " + this.U;
	}
}
