package randomreverser.math.component;

import randomreverser.util.StringUtils;

public class AugmentedMatrix {

	private Matrix base;
	private Matrix extra;

	public AugmentedMatrix(Matrix base, Matrix extra) {
		this.base = base;
		this.extra = extra;
	}

	public Matrix getBase() {
		return this.base;
	}

	public Matrix getExtra() {
		return this.extra;
	}

	public void divideRow(int y, double scalar) {
		this.base.getRow(y).divideEquals(scalar);
		this.extra.getRow(y).divideEquals(scalar);
	}

	public void subtractScaledRow(int y1, double scalar, int y2) {
		this.base.getRow(y1).subtractEquals(this.base.getRow(y2).multiply(scalar));
		this.extra.getRow(y1).subtractEquals(this.extra.getRow(y2).multiply(scalar));
	}

	public void nullifyRow(int y) {
		this.base.setRow(y, null);
		this.extra.setRow(y, null);
	}

	@Override
	public String toString() {
		return StringUtils.tableToString(Math.max(base.getHeight(), extra.getHeight()), base.getWidth() + extra.getWidth(), (row, column) -> {
			if (column < base.getWidth()) {
				if (row >= base.getHeight()) {
					return "";
				} else {
					return String.valueOf(base.get(row, column));
				}
			} else {
				column -= base.getWidth();
				if (row >= extra.getHeight()) {
					return "";
				} else {
					return String.valueOf(extra.get(row, column));
				}
			}
		}, (row, column) -> {
			if (column == 0) {
				return "[";
			} else if (column == base.getWidth()) {
				return "|";
			} else if (column == base.getWidth() + extra.getWidth()) {
				return "]";
			} else {
				return " ";
			}
		});
	}

}
