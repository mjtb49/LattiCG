package com.seedfinding.latticg.math.lattice.enumeration;

import com.seedfinding.latticg.math.component.BigMatrix;
import com.seedfinding.latticg.math.component.BigMatrixUtil;
import com.seedfinding.latticg.math.component.BigVector;
import com.seedfinding.latticg.math.lattice.enumerate.EnumerateRt;
import com.seedfinding.latticg.math.optimize.Optimize;

import java.util.stream.Stream;

public class Enumerate {
    public static Stream<BigVector> enumerate(BigMatrix basis, BigVector origin, Optimize constraints) {
        BigMatrix rootInverse = BigMatrixUtil.inverse(basis);
        BigVector rootOrigin = rootInverse.multiply(origin);

        return EnumerateRt.enumerate(basis, origin, constraints, rootInverse, rootOrigin);
    }

    @Deprecated // TODO: remove
    public static Stream<BigVector> enumerate(BigMatrix basis, BigVector lower, BigVector upper, BigVector origin) {
        Optimize.Builder builder = Optimize.Builder.ofSize(basis.getRowCount());

        for (int i = 0; i < basis.getRowCount(); ++i) {
            builder.withLowerBound(i, lower.get(i)).withUpperBound(i, upper.get(i));
        }

        return enumerate(basis, origin, builder.build());
    }

}
