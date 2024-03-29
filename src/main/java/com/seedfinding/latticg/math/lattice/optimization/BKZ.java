package com.seedfinding.latticg.math.lattice.optimization;

import com.seedfinding.latticg.math.component.BigFraction;
import com.seedfinding.latticg.math.component.BigMatrix;
import com.seedfinding.latticg.math.component.BigVector;
import com.seedfinding.latticg.math.lattice.LLL.LLL;
import com.seedfinding.latticg.math.lattice.LLL.Params;
import com.seedfinding.latticg.math.lattice.LLL.Result;

import java.math.BigInteger;

public class BKZ {
    private final BigFraction redFudgeFactor;
    private final Params params;
    private final LLL lll;
    BigFraction[] cT;
    BigFraction[] y;
    BigInteger[] v;
    BigInteger[] delta;
    BigInteger[] d;
    BigVector u;
    BigInteger[] uT;
    private BigMatrix basis;
    private BigMatrix baseGSO; // this is the Gram-Schmidt (almost Orthogonal thus GSO) basis
    private BigMatrix mu; // those are the mu that are used in the Gram-Schimdt process*
    private BigVector norms; // those are simply the magnitude of each GS vectors
    private int nbRows;
    private int nbCols;
    private BigVector BKZConstant = null;
    private BigVector BKZTresh = null;

    public BKZ(BigMatrix lattice, Params params) {
        this.params = params;
        this.basis = lattice.copy();
        this.nbRows = lattice.getRowCount();
        this.nbCols = lattice.getColumnCount();
        this.lll = new LLL(lattice, params);
        this.baseGSO = new BigMatrix(this.nbRows, this.nbCols);
        this.mu = new BigMatrix(this.nbRows, this.nbRows);
        this.norms = new BigVector(this.nbRows);
        this.redFudgeFactor = calulateFudge(51); // change here the precision you want
        cT = new BigFraction[nbRows + 2];
        v = new BigInteger[nbRows + 2];
        y = new BigFraction[nbRows + 2];
        u = new BigVector(nbRows + 2);
        uT = new BigInteger[nbRows + 2];
        delta = new BigInteger[nbRows + 2];
        d = new BigInteger[nbRows + 2];
    }

    /**
     * The BKZ algorithm as described
     *
     * @param lattice   the lattice to reduce
     * @param blockSize the block size to use in BKZ
     * @param params    the parameters to be passed to LLL
     * @return the reduced lattice
     */
    public static Result reduce(BigMatrix lattice, int blockSize, Params params) {
        if (blockSize < 2 || blockSize > lattice.getRowCount()) {
            throw new IllegalArgumentException("Invalid blocksize: " + blockSize + " for range 2-" + lattice.getRowCount());
        }
        if (blockSize > 100) {
            throw new IllegalArgumentException("BlockSize not supported: " + blockSize);
        }
        return new BKZ(lattice, params).reduceBKZ(lattice, blockSize);
    }

    public static Result reduce(BigMatrix lattice, int blockSize) {
        if (blockSize < 2 || blockSize > lattice.getRowCount()) {
            throw new IllegalArgumentException("Invalid blocksize: " + blockSize + " for range 2-" + lattice.getRowCount());
        }
        if (blockSize > 100) {
            throw new IllegalArgumentException("BlockSize not supported: " + blockSize);
        }
        return new BKZ(lattice, new Params()).reduceBKZ(lattice, blockSize);
    }


    private boolean passvec(BigVector v, int index, int dim) {
        int i;

        if (!v.get(index).equals(BigFraction.ONE)) {
            return false;
        }
        for (i = 0; i < dim; i++) {
            if (i != index && !v.get(i).equals(BigFraction.ZERO)) {
                return false;
            }
        }
        return true;
    }

    /**
     * BKZ reduces a lattice generated by a linearly independant set of vectors.
     *
     * @param lattice the lattice to BKZ reduce
     * @param beta    the blockSize to use in BKZ
     * @return a result encapsulating the lattice and the transformations done by BKZ
     */
    private Result reduceBKZ(BigMatrix lattice, int beta) {
        int k, h;
        int z = 0;
        int j = 0;
        this.basis = lattice.copy();
        Result result = LLL.reduce(basis, params);
        updateWithResult(result);
        if (params.pruneFactor > 0) {
            BKZConstant = calculateBKZConstant(beta, params.pruneFactor); // this is cost intensive
        }
        boolean clean = true;
        while (z < nbRows - 1) {
            j++;
            k = Math.min(j + beta - 1, nbRows);
            if (j == nbRows) {
                j = 1;
                k = beta;
                clean = true;
            }
            if (params.pruneFactor > 0) {
                BKZTresh = computeBKZThresh(j, k - j + 1);
            }
            Object[] objects = enumerateBKZ(j, k, nbRows, norms, mu);
            BigVector uvec = (BigVector) objects[1];
            BigFraction cbar = (BigFraction) objects[0];
            h = Math.min(k + 1, nbRows);
            // (delta-8*red_fudge) * norms[jj] > cbar
            if ((params.delta.subtract(redFudgeFactor.multiply(8))).multiply(norms.get(j - 1)).compareTo(cbar) > 0) {
                clean = false;
                int s = 0;
                for (int i = j + 1; i <= k; i++) {
                    if (!uvec.get(i).equals(BigFraction.ZERO)) {
                        s = s == 0 ? i : -1;
                    }
                }
                if (s == 0) {
                    System.err.println("Huge error, impossible case for s");
                }
                if (s > 0) {
                    //we treat the case that the new vector is b[s] (j < s <= k)
                    basis.shiftRows(j - 1, s - 1);
                    //baseGSO.shiftRows(j-1,s-1);
                    //norms.shiftElements(j-1,s-1);
                    result = LLL.reduce(basis, params);
                    updateWithResult(result);
                } else {
                    //general case
                    BigVector newVec = new BigVector(nbCols);
                    for (int i = 0; i < nbCols; i++) {
                        newVec.set(i, BigFraction.ZERO);
                    }
                    for (int i = j; i <= k; i++) {
                        if (uvec.get(i).equals(BigFraction.ZERO)) {
                            continue;
                        }
                        for (int l = 0; l < nbCols; l++) {
                            newVec.set(l, newVec.get(l).add(uvec.get(i).multiply(basis.get(i - 1, l))));
                        }
                    }

                    BigMatrix newBlock = new BigMatrix(nbRows + 1, nbCols);
                    for (int row = 0; row <= j - 2; row++) {
                        // set row 0 to j-2 (eq to 1 to j-1)
                        newBlock.setRow(row, basis.getRow(row));
                    }
                    // set row j-1 (eq j)
                    newBlock.setRow(j - 1, newVec);
                    // set row j to h+1
                    for (int row = j - 1; row < nbRows; row++) {
                        newBlock.setRow(row + 1, basis.getRow(row));
                    }
                    result = LLL.reduce(newBlock, params);
                    updateWithResult(result);
                }
                z = 0;
            } else {
                z = z + 1;
                if (!clean) {
                    result = LLL.reduce(basis, params);
                    updateWithResult(result);
                }
            }
        }
        return result;
    }

    private void updateWithResult(Result result) {
        this.nbRows = result.getReducedBasis().getRowCount();
        this.nbCols = result.getReducedBasis().getColumnCount();
        this.baseGSO = result.getGramSchmidtBasis().copy();
        this.mu = result.getGramSchmidtCoefficients().copy();
        this.norms = result.getGramSchmidtSizes().copy();
        basis = result.getReducedBasis().copy();

    }

    private Object[] enumerateBKZ(int jj, int k, int dim, BigVector norms, BigMatrix blockMu) {

        BigInteger auxUT;
        BigFraction cbar, auxY;
        int s = jj, t = jj;

        // Initialize vectors
        cbar = norms.get(jj - 1);
        d[jj] = uT[jj] = BigInteger.ONE;
        u.set(jj, BigFraction.ONE);
        delta[jj] = v[jj] = BigInteger.ZERO;
        y[jj] = BigFraction.ZERO;

        for (int i = jj + 1; i <= k + 1; i++) {
            uT[i] = delta[i] = v[i] = BigInteger.ZERO;
            u.set(i, BigFraction.ZERO);
            cT[i] = y[i] = BigFraction.ZERO;
            d[i] = BigInteger.ONE;
        }
        BigFraction eta;
        while (t <= k) {

            // cT[t] = cT[t + 1] + (auxY[t] - 2*uT[t]*y[t] + auxUT[t]) * B[t];
            // cT(t) := cT(t+1) + (y(t) + u(t))^2 * c(t)  but (y(t)+u(t))^2= y(t)^2 + u(t)^2 + 2*u(t)*y(t)
            auxY = y[t].multiply(y[t]); // this is done to overcome loss in precision remember how they cumulate...
            auxUT = uT[t].multiply(uT[t]);
            cT[t] = cT[t + 1].add((auxY.add(y[t].multiply(uT[t]).multiply(BigFraction.TWO)).add(auxUT)).multiply(norms.get(t - 1)));
            if (params.pruneFactor > 0 && t > jj) {
                eta = BKZTresh.get(t - jj - 1);
            } else {
                eta = BigFraction.ZERO;
            }
            if (cT[t].compareTo(cbar.subtract(eta)) < 0) {
                if (t > jj) {
                    t--;
                    y[t] = BigFraction.ZERO;
                    for (int i = t + 1; i <= s; i++) {
                        y[t] = y[t].add(blockMu.get(i - 1, t - 1).multiply(uT[i]));
                    }
                    uT[t] = v[t] = y[t].round().negate();
                    delta[t] = BigInteger.ZERO;
                    // if (uT[t] > -y[t]) -> -y[t]< uT[t]
                    if (y[t].negate().compareTo(uT[t]) < 0) {
                        d[t] = BigInteger.ONE.negate();
                    } else {
                        d[t] = BigInteger.ONE;
                    }
                } else {
                    cbar = cT[jj];
                    for (int j = jj; j <= k; j++) {
                        u.set(j, new BigFraction(uT[j]));
                    }
                }
            } else {
                t++;
                s = Math.max(s, t); //Get max value
                if (t < s) {
                    delta[t] = delta[t].negate();
                }
                if (delta[t].multiply(d[t]).compareTo(BigInteger.ZERO) >= 0) {
                    delta[t] = delta[t].add(d[t]);
                }
                uT[t] = v[t].add(delta[t]);
            }
        }
        return new Object[] {cbar, u};
    }

    private void printVec(BigFraction[] vec, String msg) {
        System.out.print("vec " + msg + " =");
        for (BigFraction bigFraction : vec) {
            if (bigFraction == null) {
                System.out.print("0 ");
            } else {
                System.out.print(bigFraction.toDouble() + " ");
            }
        }
        System.out.println();
    }

    private static BigFraction calulateFudge(int precision) {
        BigFraction fudge = BigFraction.ONE;
        for (int i = precision; i > 0; i--) {
            fudge = fudge.multiply(BigFraction.HALF);
        }
        return fudge;
    }

    private BigVector calculateBKZConstant(int beta, int p) {
        BigVector res = new BigVector(beta - 1);
        BigVector log = new BigVector(beta);

        for (int j = 1; j <= beta; j++) {
            log.set(j, BigVector.LOG_TABLE.get(j));
        }
        BigFraction x, y;
        for (int i = 1; i <= beta - 1; i++) {
            // First, we compute x = gamma(i/2)^{2/i}
            BigFraction k = new BigFraction(i).divide(BigFraction.TWO);
            if ((i & 1) == 0) { // i even
                x = BigFraction.ZERO;
                for (int j = 1; j <= k.toDouble(); j++) {
                    x = x.add(log.get(j));
                }
                x = x.multiply(BigFraction.ONE.divide(k));
                x = x.exp();
            } else { // i odd
                x = BigFraction.ZERO;
                for (int j = k.round().intValue() + 2; j <= 2 * k.toDouble() + 2; j++) {
                    x = x.add(log.get(j));
                }
                x = BigFraction.HALF.multiply(BigFraction.LOG_PI).add(x).subtract((k.add(BigInteger.ONE).multiply(log.get(2))).multiply(BigFraction.TWO));
                x = x.multiply(new BigFraction(2, i));
                x = x.exp();
            }
            // Second, we compute y = 2^{2*p/i}
            y = new BigFraction(-2, i).multiply(log.get(2)).multiply(p);
            y = y.exp();
            res.set(i - 1, x.multiply(y).divide(BigFraction.PI));
        }
        return res;
    }

    private BigVector computeBKZThresh(int j, int beta) {
        BigVector res = new BigVector(beta - 1);
        BigFraction x = BigFraction.ZERO;
        for (int i = 0; i < beta - 1; i++) {
            x.add(norms.get(j + i).log());
            res.set(i, x.divide(new BigFraction(i + 1)).exp().multiply(BKZConstant.get(i)));
        }
        return res;
    }

}
