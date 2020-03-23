package kaptainwutax.seedutils.math.component;

public class GenArray<T extends IComponent<T>> {

	protected Object[] grid;
	private int length;

	public GenArray(int length) {
		this.grid = new Object[length];
		this.length = length;
	}

	public T get(int i) {
		return (T)this.grid[i];
	}

	public void set(int i, T value) {
		this.grid[i] = value;
	}

	public int getLength() {
		return this.length;
	}

	public void swap(int i, int j) {
		Object temp = this.grid[i];
		this.grid[i] = this.grid[j];
		this.grid[j] = temp;
	}

}
