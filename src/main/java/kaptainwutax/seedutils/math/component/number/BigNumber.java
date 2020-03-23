package kaptainwutax.seedutils.math.component.number;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigNumber extends NumberType<BigDecimal, BigNumber> {

	public BigNumber(double initialValue) {
		this(BigDecimal.valueOf(initialValue).setScale(5, RoundingMode.HALF_UP));
	}

	public BigNumber(BigDecimal initialValue) {
		super(initialValue);
	}

	@Override
	public BigNumber getZero() {
		return new BigNumber(BigDecimal.ZERO);
	}

	@Override
	public BigDecimal getRaw() {
		return this.getValue();
	}

	@Override
	public BigNumber round() {
		return new BigNumber(this.getValue().setScale(0, RoundingMode.HALF_UP));
	}

	@Override
	public BigNumber add(BigNumber a) {
		return new BigNumber(this.getValue().add(a.getValue()));
	}

	@Override
	public BigNumber subtract(BigNumber a) {
		return new BigNumber(this.getValue().subtract(a.getValue()));
	}

	@Override
	public BigNumber multiply(BigNumber a) {
		return new BigNumber(this.getValue().multiply(a.getValue()));
	}

	@Override
	public BigNumber divide(BigNumber a) {
		return new BigNumber(this.getValue().divide(a.getValue(), RoundingMode.HALF_UP));
	}

	@Override
	public void addEquals(BigNumber a) {
		this.setValue(this.getValue().add(a.getValue()));
	}

	@Override
	public void subtractEquals(BigNumber a) {
		this.setValue(this.getValue().subtract(a.getValue()));
	}

	@Override
	public void multiplyEquals(BigNumber a) {
		this.setValue(this.getValue().multiply(a.getValue()));
	}

	@Override
	public void divideEquals(BigNumber a) {
		this.setValue(this.getValue().divide(a.getValue(), RoundingMode.HALF_UP));
	}

	@Override
	public BigNumber copy() {
		return new BigNumber(this.getValue());
	}

	@Override
	public String toString() {
		return this.getValue().stripTrailingZeros().toPlainString();
	}

}
