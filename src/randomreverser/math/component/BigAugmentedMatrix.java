package randomreverser.math.component;

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
		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < this.base.getHeight(); i++) {
			sb.append("[");

			for(int j = 0; j < this.base.getWidth(); j++) {
				sb.append(" ").append(this.base.get(i, j) == null ? null : this.base.get(i, j).stripTrailingZeros().toPlainString()).append(" ");
			}

			sb.append("|");

			for(int j = 0; j < this.extra.getWidth(); j++) {
				sb.append(" ").append(this.extra.get(i, j) == null ? null : this.extra.get(i, j).stripTrailingZeros().toPlainString()).append(" ");
			}

			sb.append("]\n");
		}

		return sb.toString();
	}

}
