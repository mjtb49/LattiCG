package randomreverser.math.lattice;

import randomreverser.math.component.BigFraction;
import randomreverser.math.component.BigMatrix;
import randomreverser.math.component.BigVector;

import java.math.BigInteger;
import java.util.*;

class SearchNode {
    private final int size;
    private final int depth;

    private final BigMatrix transform;
    private final BigVector offset;
    private final BigMatrix table;
    private final BigVector fixed;

    private Spliterator<BigVector> spliterator;

    public SearchNode(int size, int depth, BigMatrix transform, BigVector offset, BigMatrix table, BigVector fixed) {
        this.size = size;
        this.depth = depth;
        this.transform = transform;
        this.offset = offset;
        this.table = table;
        this.fixed = fixed;
    }

    private void initialize() {
        if (this.depth == this.size) {
            this.spliterator = Collections.singleton(this.fixed).spliterator();
            return;
        }

        List<SearchNode> children = new ArrayList<>();

        BigVector x;
        BigInteger min, max;
        BigVector transformRow = this.transform.getRow(this.depth);

        for (int col = 0; col < this.size; ++col) {
            this.table.set(0, col, transformRow.get(col).negate());
        }

        x = Optimize.optimize(this.table, this.size, this.depth);
        min = transformRow.dot(x).add(this.offset.get(this.depth)).ceil();

        for (int col = 0; col < this.size; ++col) {
            this.table.set(0, col, transformRow.get(col));
        }

        x = Optimize.optimize(this.table, this.size, this.depth);
        max = transformRow.dot(x).add(this.offset.get(this.depth)).floor();

        max = max.add(BigInteger.ONE);

        for (; !min.equals(max); min = min.add(BigInteger.ONE)) {
            BigFraction y = this.offset.get(this.depth).subtract(min);

            if (y.signum() >= 0) {
                for (int col = 0; col < this.size; ++col) {
                    this.table.set(1 + this.size + this.depth, col, transformRow.get(col).negate());
                }

                this.table.set(1 + this.size + this.depth, this.size, y);
            } else {
                for (int col = 0; col < this.size; ++col) {
                    this.table.set(1 + this.size + this.depth, col, transformRow.get(col));
                }

                this.table.set(1 + this.size + this.depth, this.size, y.negate());
            }

            this.fixed.set(this.depth, new BigFraction(min));
            children.add(new SearchNode(this.size, this.depth + 1, this.transform, this.offset, this.table.copy(), this.fixed.copy()));
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
