package kaptainwutax.seedutils.math.component;

public class GenGrid<T extends IComponent<T>> {

	protected Object[][] grid;
	private final int height;
	private final int width;

	public GenGrid(int height, int width) {
		this.grid = new Object[height][width];
		this.height = height;
		this.width = width;
	}

	public T get(int i, int j) {
		return (T)this.grid[i][j];
	}

	public void set(int i, int j, T value) {
		this.grid[i][j] = value;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public void swap(int i, int j) {
		Object[] temp = this.grid[i];
		this.grid[i] = this.grid[j];
		this.grid[j] = temp;
	}

}
