package kaptainwutax.seedutils.math.component.number;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleNumber extends NumberType<Double, DoubleNumber> {

	public DoubleNumber(Double initialValue) {
		super(initialValue);
	}

	@Override
	public DoubleNumber getZero() {
		return new DoubleNumber(0.0D);
	}

	@Override
	public BigDecimal getRaw() {
		return BigDecimal.valueOf(this.getValue());
	}

	@Override
	public DoubleNumber round() {
		return new DoubleNumber(BigDecimal.valueOf(this.getValue()).setScale(0, RoundingMode.HALF_UP).doubleValue());
	}

	@Override
	public DoubleNumber add(DoubleNumber a) {
		return new DoubleNumber(this.getValue() + a.getValue());
	}

	@Override
	public DoubleNumber subtract(DoubleNumber a) {
		return new DoubleNumber(this.getValue() - a.getValue());
	}

	@Override
	public DoubleNumber multiply(DoubleNumber a) {
		return new DoubleNumber(this.getValue() * a.getValue());
	}

	@Override
	public DoubleNumber divide(DoubleNumber a) {
		return new DoubleNumber(this.getValue() / a.getValue());
	}

	@Override
	public void addEquals(DoubleNumber a) {
		this.setValue(this.getValue() + a.getValue());
	}

	@Override
	public void subtractEquals(DoubleNumber a) {
		this.setValue(this.getValue() - a.getValue());
	}

	@Override
	public void multiplyEquals(DoubleNumber a) {
		this.setValue(this.getValue() * a.getValue());
	}

	@Override
	public void divideEquals(DoubleNumber a) {
		this.setValue(this.getValue() / a.getValue());
	}

	@Override
	public DoubleNumber copy() {
		return new DoubleNumber(this.getValue());
	}

	@Override
	public String toString() {
		return String.valueOf(this.getValue());
	}

}
