package randomreverser.math.lattice;

import randomreverser.math.component.BigVector;

class LeafSearchSpliterator implements ISearchSpliterator {
    private BigVector value;

    public LeafSearchSpliterator(BigVector value) {
        this.value = value;
    }

    @Override
    public BigVector next() {
        BigVector result = this.value;
        this.value = null;

        return result;
    }

    @Override
    public ISearchSpliterator trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        if (this.value != null) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int characteristics() {
        return NONNULL | DISTINCT | SIZED;
    }
}
