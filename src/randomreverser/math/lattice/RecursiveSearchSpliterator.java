package randomreverser.math.lattice;

import randomreverser.math.component.BigVector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

class RecursiveSearchSpliterator implements ISearchSpliterator {
    private final LinkedList<SearchNode> children;

    public RecursiveSearchSpliterator(List<SearchNode> children) {
        this.children = new LinkedList<>(children);
    }

    public BigVector next() {
        BigVector result = null;

        while (result == null && !this.children.isEmpty()) {
            result = this.children.getFirst().spliterator().next();

            if (result == null) {
                this.children.removeFirst();
            }
        }

        return result;
    }

    @Override
    public void forEachRemaining(Consumer<? super BigVector> action) {
        while (!this.children.isEmpty()) {
            BigVector result = this.children.getFirst().spliterator().next();

            if (result == null) {
                this.children.removeFirst();
            } else {
                action.accept(result);
            }
        }
    }

    @Override
    public ISearchSpliterator trySplit() {
        if (this.children.isEmpty()) {
            return null;
        } else if (this.children.size() == 1) {
            ISearchSpliterator child = this.children.get(0).spliterator();

            if (child != null) {
                return child.trySplit();
            } else {
                return null;
            }
        }

        int count = this.children.size() / 2;
        List<SearchNode> split = new ArrayList<>();

        for (int i = 0; i < count; ++i) {
            split.add(this.children.removeFirst());
        }

        return new RecursiveSearchSpliterator(split);
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE; // TODO: attempt to give an actual estimate? don't know if it's worth it, it works fine
    }

    @Override
    public int characteristics() {
        return NONNULL | DISTINCT;
    }
}
