package com.seedfinding.latticg.math.lattice.enumerate;

import com.seedfinding.latticg.math.component.BigVector;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Spliterator;
import java.util.function.Consumer;

class SearchSpliterator implements Spliterator<BigVector> {
    private final Deque<SearchNode> children;

    public SearchSpliterator(Deque<SearchNode> children) {
        this.children = children;
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
            Spliterator<BigVector> child = this.children.getFirst().spliterator();

            if (child != null) {
                return child.trySplit();
            } else {
                return null;
            }
        }

        int count = this.children.size() / 2;
        Deque<SearchNode> split = new LinkedList<>();

        for (int i = 0; i < count; ++i) {
            split.addLast(this.children.removeFirst());
        }

        return new SearchSpliterator(split);
    }

    @Override
    public long estimateSize() {
        // TODO: attempt to give an actual estimate?
        // as it is, it's works well with streams, and is well-defined by the
        // spliterator API
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return DISTINCT | ORDERED | NONNULL | IMMUTABLE;
    }
}
