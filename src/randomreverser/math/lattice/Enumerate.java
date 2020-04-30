package randomreverser.math.lattice;

import randomreverser.math.component.*;
import randomreverser.math.optimize.Optimize;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Enumerate {
    public static Stream<BigVector> enumerate(BigMatrix basis, BigVector origin, Optimize constraints) {
        int rootSize = basis.getRowCount();
        BigMatrix rootInverse = basis.inverse();
        BigVector rootOrigin = rootInverse.multiply(origin);
        BigVector rootFixed = new BigVector(rootSize);
        Optimize rootConstraints = constraints.copy();

        List<BigFraction> widths = new ArrayList<>();
        List<Integer> order = new ArrayList<>();

        for (int i = 0; i < rootSize; ++i) {
            BigFraction min = constraints.copy().minimize(rootInverse.getRow(i)).getSecond();
            BigFraction max = constraints.copy().maximize(rootInverse.getRow(i)).getSecond();
            widths.add(max.subtract(min));
            order.add(i);
        }

        order.sort(Comparator.comparing(i -> widths.get(i)));

        try {
            SearchNode root = new SearchNode(rootSize, 0, rootInverse, rootOrigin, rootFixed, rootConstraints, order);

            return StreamSupport.stream(root.spliterator(), true)
                    .map(basis::multiply)
                    .map(origin::add);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("constraints are not feasible", e);
        }
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
