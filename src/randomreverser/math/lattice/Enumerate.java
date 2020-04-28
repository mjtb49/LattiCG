package randomreverser.math.lattice;

import randomreverser.math.component.*;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Enumerate {
    public static Stream<BigVector> enumerate(BigMatrix basis, BigVector lower, BigVector upper, BigVector offset) {
        int rootSize = basis.getRowCount();
        BigMatrix rootTransform = basis.inverse();
        BigVector rootOffset = rootTransform.multiply(lower.subtract(offset));
        BigMatrix rootTable = new BigMatrix(2 * rootSize + 1, rootSize + 1);
        BigVector rootFixed = new BigVector(rootSize);

        for (int i = 0; i < rootSize; ++i) {
            rootTable.set(i + 1, i, BigFraction.ONE);
            rootTable.set(i + 1, rootSize, upper.get(i).subtract(lower.get(i)));
        }

        SearchNode root = new SearchNode(rootSize, 0, rootTransform, rootOffset, rootTable, rootFixed);

        return StreamSupport.stream(root.spliterator(), true)
                .map(basis::multiply)
                .map(offset::add);
    }
}
