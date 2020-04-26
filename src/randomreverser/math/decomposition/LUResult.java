package randomreverser.math.decomposition;

import randomreverser.math.component.Matrix;
import randomreverser.math.component.Vector;

import java.util.regex.Pattern;

public class LUResult {

	protected final Matrix l;
	protected final Matrix u;
	protected final Vector p;

	public LUResult(Matrix m, Vector p) {
		int n = m.getRowCount();

		this.l = new Matrix(n, n, (row, col) -> {
			if(row > col)return m.get(row, col);
			else if(row == col)return 1.0D;
			else return 0.0D;
		});

		this.u = new Matrix(n, n, (row, col) -> {
			if(row <= col)return m.get(row, col);
			else return 0.0D;
		});

		this.p = p;
	}

	public Matrix getL() {
		return this.l;
	}

	public Matrix getU() {
		return this.u;
	}

	public String toPrettyString() {
		StringBuilder sb = new StringBuilder();
		String[] lStuff = this.l.toPrettyString().split(Pattern.quote("\n"));
		String[] uStuff = this.u.toPrettyString().split(Pattern.quote("\n"));

		for(int i = 0; i < lStuff.length; i++) {
			sb.append(lStuff[i]).append("  ").append(uStuff[i]).append("\n");
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return this.l + " | " + this.u;
	}
}
