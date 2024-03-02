package com.seedfinding.latticg.math.lattice.enumerate;

import com.seedfinding.latticg.math.component.BigFraction;
import com.seedfinding.latticg.math.component.BigMatrix;
import com.seedfinding.latticg.math.component.BigVector;
import com.seedfinding.latticg.math.optimize.Optimize;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class EnumerateRt {
    private EnumerateRt() {
    }

    public static Stream<BigVector> enumerate(BigMatrix basis, BigVector origin, Optimize constraints, BigMatrix rootInverse, BigVector rootOrigin) {
        int rootSize = basis.getRowCount();
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
}
