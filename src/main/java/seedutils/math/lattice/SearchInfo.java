package main.java.seedutils.math.lattice;

import main.java.seedutils.math.component.Matrix;

import java.util.List;

public class SearchInfo {

	public long dimensions;
	public long depth;

	public Matrix transform;
	public Matrix offset;
	public Matrix fixed;

	public Matrix table;
	public Matrix x;

	public List<Matrix> result;

}
