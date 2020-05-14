package randomreverser.math.lattice;

import randomreverser.math.component.BigFraction;
import randomreverser.math.component.BigMatrix;
import randomreverser.math.component.BigVector;

import java.math.BigInteger;

// GSO stands for Gram-Schmidt Orthogonalization
public class LLLbis {
    private final BigMatrix baseGSO;
    private final BigMatrix mu;
    private final int nbRows;
    private final int nbCols;
    private final BigVector QNorms;
    private BigMatrix basis;

    public LLLbis(BigMatrix lattice) {
        this.basis = lattice.copy();
        this.nbRows = lattice.getRowCount();
        this.nbCols = lattice.getColumnCount();
        this.baseGSO = new BigMatrix(this.nbRows, this.nbCols);
        this.mu = new BigMatrix(this.nbRows, this.nbRows);
        this.QNorms = new BigVector(this.nbRows);
    }

    /**
     * LLL lattice reduction implemented as described on page 95 of Henri Cohen's
     * "A course in computational number theory"
     *
     * @param lattice the lattice to reduce
     * @param params  the parameters to be passed to LLL
     * @return the reduced lattice
     */
    public static LLLbis.Result reduce(BigMatrix lattice, LLLbis.Params params) {
        return new LLLbis(lattice).reduceLLL(lattice, params);
    }

    private BigFraction innerProduct(BigVector vector1, BigVector vector2) {
        return vector1.dot(vector2);
    }

    private void computeGSO() {
        baseGSO.setRow(0, basis.getRow(0));
        QNorms.set(0, innerProduct(baseGSO.getRow(0), baseGSO.getRow(0)));   //<bi,bi> equals to ||bi||^2
        for (int i = 1; i < nbRows; i++) {
            baseGSO.setRow(i, basis.getRow(i));
            for (int j = 0; j < i; j++) {
                if (QNorms.get(j).compareTo(BigFraction.ZERO) == 0) {
                    mu.set(i, j, BigFraction.ZERO);
                } else {
                    mu.set(i, j, innerProduct(basis.getRow(i), baseGSO.getRow(j)).divide(QNorms.get(j)));
                }
                for (int k = 0; k < nbCols; k++) {
                    baseGSO.set(i, k, baseGSO.get(i, k).subtract(mu.get(i, j).multiply(baseGSO.get(j, k))));
                }
            }
            QNorms.set(i, innerProduct(baseGSO.getRow(i), baseGSO.getRow(i)));
        }
        /*

        for (int j = 0; j < nbRows; j++) {
            if (QNorms.get(k).compareTo(BigFraction.ZERO) != 0) {
                mu.set(k, j, innerProduct(basis.getRow(k),baseGSO.getRow(j)).divide(QNorms[j]));
            } else {
                mu.set(k, j, BigFraction.ZERO);
            }
        }
        BigVector newRow = basis.getRow(k).copy();
        for (int i = 0; i <= k - 1; i++) {
            newRow.subtractEquals(baseGSO.getRow(i).multiply(mu.get(k, i)));
        }
        baseGSO.setRow(k, newRow);
        QNorms.set(k,newRow.magnitudeSq());
         */
    }

    BigFraction breakCondition(int k, int kl) {
        BigFraction res = QNorms.get(kl);
        for (int i = k - 1; i < kl; i++) {
            res = res.add(mu.get(kl, i).multiply(mu.get(kl, i)).multiply(QNorms.get(i)));
        }
        return res;
    }

    private void sizeReduction(BigMatrix basis, int k) {
        // babai nearest plane algorithm, beware here k is already k-1 so j start indeed at k-2
        BigInteger r;
        for (int row = k-1 ; row >= 0; row--) {
            r=mu.get(k,row).round();
            basis.getRow(k).subtractEquals(basis.getRow(row).multiply(r));
            for (int cols = 0; cols < row; cols++) {
                mu.set(k, cols,mu.get(k, cols).subtract(mu.get(row, cols).multiply(r)));
            }
        }
    }

    private void updateGSO(int k){

    }
    private boolean testCondition(int stage,BigFraction delta){
        BigFraction muTemp=mu.get(stage,stage-1);
        BigFraction factor=delta.subtract(muTemp.multiply(muTemp));
        return QNorms.get(stage-1).multiply(factor).compareTo(QNorms.get(stage))<=0;
    }
    // that's not a floating friendly its L3-red
    private LLLbis.Result reduceLLL(BigMatrix lattice, LLLbis.Params params) {
        this.basis = lattice.copy();
        BigFraction delta = params.delta;
        int maxStage = params.maxStage == -1 ? lattice.getRowCount() : params.maxStage;
        int stage = 1;
        computeGSO();
        while (stage < maxStage) {
            sizeReduction(basis, stage);
            if (testCondition(stage,delta)) {
                stage++;
            }else{
                basis.swapRows(stage,stage-1);
                computeGSO();
                stage=Math.max(stage-1,1);
            }
            System.out.println(stage);
        }
        int p = 0;
        for (int i = 0; i < nbRows; i++) {
            if (this.basis.getRow(i).isZero()) {
                p++;
            }
        }
        //remove all zero vectors
        BigMatrix nonZeroLattice = this.basis.submatrix(p, 0, nbRows - p, nbCols);
        return new Result(p, nonZeroLattice, lattice);
    }

    public static final class Result {
        private final int numDependantVectors;
        private final BigMatrix reducedBasis;
        private final BigMatrix transformationsDone;
        private BigMatrix gramSchmidtBasis;
        private BigMatrix gramSchmidtCoefficients;
        private BigFraction[] gramSchmidtSizes;

        private Result(int numDependantVectors, BigMatrix reducedBasis, BigMatrix transformationsDone) {
            this.numDependantVectors = numDependantVectors;
            this.reducedBasis = reducedBasis;
            this.transformationsDone = transformationsDone;
        }

        private LLLbis.Result setGramSchmidtInfo(BigMatrix gramSchmidtBasis, BigMatrix GSCoefficients, BigFraction[] GSSizes) {
            this.gramSchmidtBasis = gramSchmidtBasis;
            this.gramSchmidtCoefficients = GSCoefficients;
            this.gramSchmidtSizes = GSSizes;
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

        public BigFraction[] getGramSchmidtSizes() {
            return gramSchmidtSizes;
        }
    }

    public static final class Params {
        protected BigFraction delta = new BigFraction(75, 100);
        protected boolean debug;
        protected int maxStage = -1;

        public LLLbis.Params setMaxStage(int maxStage) {
            this.maxStage = maxStage;
            return this;
        }

        public LLLbis.Params setDelta(BigFraction delta) {
            this.delta = delta;
            return this;
        }

        public LLLbis.Params setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }
    }
}
