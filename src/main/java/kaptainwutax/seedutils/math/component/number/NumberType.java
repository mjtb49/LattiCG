package kaptainwutax.seedutils.math.component.number;

import kaptainwutax.seedutils.math.component.IComponent;
import kaptainwutax.seedutils.math.component.ICopy;

import java.math.BigDecimal;

public abstract class NumberType<T, S extends NumberType<T, S>> implements IComponent<S>, ICopy<S> {

	private T value;

	public NumberType(T initialValue) {
		this.value = initialValue;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public abstract S getZero();

	public void setToZero() {
		this.setValue(this.getZero().getValue());
	}

	public boolean isZero() {
		return this.value.equals(this.getZero().getValue());
	}

	public abstract BigDecimal getRaw();

	public abstract S round();

}
