package kaptainwutax.seedutils.math.component.number;

import java.math.BigDecimal;

public class IntNumber extends NumberType<Integer, IntNumber> {

	public IntNumber(Integer initialValue) {
		super(initialValue);
	}

	@Override
	public IntNumber getZero() {
		return new IntNumber(0);
	}

	@Override
	public BigDecimal getRaw() {
		return new BigDecimal(this.getValue());
	}

	@Override
	public IntNumber round() {
		return new IntNumber(this.getValue());
	}

	@Override
	public IntNumber add(IntNumber a) {
		return new IntNumber(this.getValue() + a.getValue());
	}

	@Override
	public IntNumber subtract(IntNumber a) {
		return new IntNumber(this.getValue() - a.getValue());
	}

	@Override
	public IntNumber multiply(IntNumber a) {
		return new IntNumber(this.getValue() * a.getValue());
	}

	@Override
	public IntNumber divide(IntNumber a) {
		return new IntNumber(this.getValue() / a.getValue());
	}

	@Override
	public void addEquals(IntNumber a) {
		this.setValue(this.getValue() + a.getValue());
	}

	@Override
	public void subtractEquals(IntNumber a) {
		this.setValue(this.getValue() - a.getValue());
	}

	@Override
	public void multiplyEquals(IntNumber a) {
		this.setValue(this.getValue() * a.getValue());
	}

	@Override
	public void divideEquals(IntNumber a) {
		this.setValue(this.getValue() / a.getValue());
	}

	@Override
	public IntNumber copy() {
		return new IntNumber(this.getValue());
	}

	@Override
	public String toString() {
		return String.valueOf(this.getValue());
	}

}
