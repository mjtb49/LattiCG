package randomreverser.math.component;

import randomreverser.util.StringUtils;

import java.math.BigDecimal;

public class BigAugmentedMatrix {

	private BigMatrix base;
	private BigMatrix extra;

	public BigAugmentedMatrix(BigMatrix base, BigMatrix extra) {
		this.base = base;
		this.extra = extra;
	}

	public BigMatrix getBase() {
		return this.base;
	}

	public BigMatrix getExtra() {
		return this.extra;
	}

	public void divideRow(int y, BigDecimal scalar) {
		this.base.getRow(y).divideEquals(scalar);
		this.extra.getRow(y).divideEquals(scalar);
	}

	public void subtractScaledRow(int y1, BigDecimal scalar, int y2) {
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
					return base.get(row, column) == null ? "null" :
							base.get(row, column).stripTrailingZeros().toPlainString();
				}
			} else {
				column -= base.getWidth();
				if (row >= extra.getHeight()) {
					return "";
				} else {
					return extra.get(row, column) == null ? "null" :
							extra.get(row, column).stripTrailingZeros().toPlainString();
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
