package randomreverser.math.lattice;

import randomreverser.math.component.BigVector;

import java.util.Spliterator;
import java.util.function.Consumer;

interface ISearchSpliterator extends Spliterator<BigVector> {
    BigVector next();
    ISearchSpliterator trySplit();

    default boolean tryAdvance(Consumer<? super BigVector> action) {
        BigVector result = this.next();

        if (result != null) {
            action.accept(result);
            return true;
        } else {
            return false;
        }
    }
}
