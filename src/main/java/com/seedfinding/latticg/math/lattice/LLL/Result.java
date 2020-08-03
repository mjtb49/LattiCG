package com.seedfinding.latticg.math.lattice.LLL;

import com.seedfinding.latticg.math.component.BigMatrix;
import com.seedfinding.latticg.math.component.BigVector;

public class Result {
    private final int numDependantVectors;
    private final BigMatrix reducedBasis;
    private final BigMatrix transformationsDone;
    private BigMatrix gramSchmidtBasis;
    private BigMatrix gramSchmidtCoefficients;
    private BigVector gramSchmidtSizes;

    public Result(int numDependantVectors, BigMatrix reducedBasis, BigMatrix transformationsDone) {
        this.numDependantVectors = numDependantVectors;
        this.reducedBasis = reducedBasis;
        this.transformationsDone = transformationsDone;
    }

    public Result setGramSchmidtInfo(BigMatrix gramSchmidtBasis, BigMatrix GSCoefficients, BigVector norms) {
        this.gramSchmidtBasis = gramSchmidtBasis;
        this.gramSchmidtCoefficients = GSCoefficients;
        this.gramSchmidtSizes = norms;
        return this;
    }

    public int getNumDependantVectors() {
        return numDependantVectors;
    }

    public BigMatrix getReducedBasis() {
        return reducedBasis;
    }

    public BigMatrix getTransformations() {
        return transformationsDone;
    }

    public BigMatrix getGramSchmidtBasis() {
        return gramSchmidtBasis;
    }

    public BigMatrix getGramSchmidtCoefficients() {
        return gramSchmidtCoefficients;
    }

    public BigVector getGramSchmidtSizes() {
        return gramSchmidtSizes;
    }
}
