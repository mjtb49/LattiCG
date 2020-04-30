package randomreverser.math.lattice;

import randomreverser.math.component.*;
import randomreverser.math.optimize.Optimize;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Enumerate {
    public static Stream<BigVector> enumerate(BigMatrix basis, BigVector origin, Optimize constraints) {
        int rootSize = basis.getRowCount();
        BigMatrix rootTransform = basis.inverse();
        BigVector rootOrigin = rootTransform.multiply(origin);
        BigVector rootFixed = new BigVector(rootSize);
        Optimize rootConstraints = constraints.copy();

        // we want points of the form offset + basis * x that satisfy constraints

        // TODO: check lattice aligned widths of bounding box to get a rough idea of what order to traverse in

        SearchNode root = new SearchNode(rootSize, 0, rootTransform, rootOrigin, rootFixed, rootConstraints);

        return StreamSupport.stream(root.spliterator(), true)
                .map(basis::multiply)
                .map(origin::add);
    }

    // TODO: remove
    public static Stream<BigVector> enumerate(BigMatrix basis, BigVector lower, BigVector upper, BigVector origin) {
        Optimize.Builder builder = Optimize.Builder.ofSize(basis.getRowCount());

        for (int i = 0; i < basis.getRowCount(); ++i) {
            builder.withLowerBound(i, lower.get(i)).withUpperBound(i, upper.get(i));
        }

        return enumerate(basis, origin, builder.build());
    }
}
