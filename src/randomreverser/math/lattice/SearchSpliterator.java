package randomreverser.math.lattice;

import randomreverser.math.component.BigVector;

import java.util.*;
import java.util.function.Consumer;

class SearchSpliterator implements Spliterator<BigVector> {
    private final LinkedList<SearchNode> children;

    public SearchSpliterator(List<SearchNode> children) {
        this.children = new LinkedList<>(children);
    }

    public boolean tryAdvance(Consumer<? super BigVector> action) {
        while (!this.children.isEmpty()) {
            if (this.children.getFirst().spliterator().tryAdvance(action)) {
                return true;
            } else {
                this.children.removeFirst();
            }
        }

        return false;
    }

    @Override
    public void forEachRemaining(Consumer<? super BigVector> action) {
        while (!this.children.isEmpty()) {
            this.children.removeFirst().spliterator().forEachRemaining(action);
        }
    }

    @Override
    public Spliterator<BigVector> trySplit() {
        if (this.children.isEmpty()) {
            return null;
        } else if (this.children.size() == 1) {
            Spliterator<BigVector> child = this.children.get(0).spliterator();

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

        return new SearchSpliterator(split);
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE; // TODO: attempt to give an actual estimate? don't know if it's worth it, it works fine
    }

    @Override
    public int characteristics() {
        return NONNULL | DISTINCT | IMMUTABLE | ORDERED;
    }
}
