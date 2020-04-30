package randomreverser.math.lattice;

import randomreverser.math.component.BigFraction;
import randomreverser.math.component.BigMatrix;
import randomreverser.math.component.BigVector;
import randomreverser.math.optimize.Optimize;

import java.math.BigInteger;
import java.util.*;

class SearchNode {
    private final int size;
    private final int depth;

    private final BigMatrix inverse;
    private final BigVector origin;
    private final BigVector fixed;
    private final Optimize constraints;

    private Spliterator<BigVector> spliterator;

    public SearchNode(int size, int depth, BigMatrix inverse, BigVector origin, BigVector fixed, Optimize constraints) {
        this.size = size;
        this.depth = depth;
        this.inverse = inverse;
        this.origin = origin;
        this.fixed = fixed;
        this.constraints = constraints;
    }

    private void initialize() {
        if (this.depth == this.size) {
            this.spliterator = Collections.singleton(this.fixed).spliterator();
            return;
        }

        Deque<SearchNode> children = new LinkedList<>();

        BigVector gradient = this.inverse.getRow(this.depth);
        BigFraction offset = this.origin.get(this.depth);

        // make copies since if we try to do max after min, we force the
        // optimizer to retrace its steps
        BigInteger min = this.constraints.copy().minimize(gradient).getSecond().subtract(offset).ceil();
        BigInteger max = this.constraints.copy().maximize(gradient).getSecond().subtract(offset).floor();

        for (; min.compareTo(max) <= 0; min = min.add(BigInteger.ONE)) {
            Optimize next = this.constraints.withStrictBound(gradient, new BigFraction(min).add(offset));
            this.fixed.set(this.depth, new BigFraction(min));
            children.addLast(new SearchNode(this.size, this.depth + 1, this.inverse, this.origin, this.fixed.copy(), next));
        }

        this.spliterator = new SearchSpliterator(children);
    }

    public Spliterator<BigVector> spliterator() {
        if (this.spliterator == null) {
            this.initialize();
        }

        return this.spliterator;
    }
}
