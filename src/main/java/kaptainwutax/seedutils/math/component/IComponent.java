package kaptainwutax.seedutils.math.component;

public interface IComponent<T> extends ICopy<T> {

	T add(T a);

	T subtract(T a);

	T multiply(T a);

	T divide(T a);

	void addEquals(T a);

	void subtractEquals(T a);

	void multiplyEquals(T a);

	void divideEquals(T a);



}
