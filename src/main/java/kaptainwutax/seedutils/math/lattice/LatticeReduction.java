package kaptainwutax.seedutils.math.lattice;

import kaptainwutax.seedutils.math.component.Basis;
import kaptainwutax.seedutils.math.component.number.NumberType;

public abstract class LatticeReduction<T extends NumberType<?, T>, P> {

	public abstract Basis<T> reduce(Basis<T> b, P params);

}
