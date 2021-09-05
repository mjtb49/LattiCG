package com.seedfinding.latticg.math.lattice.LLL;

import com.seedfinding.latticg.math.component.BigFraction;
import com.seedfinding.latticg.math.component.BigMatrix;
import com.seedfinding.latticg.math.component.BigVector;

import java.math.BigInteger;
// @formatter:off
/**
 * This algorithm is as described in A course in Computational Algebraic Number Theory
 * The run down is: Given a basis b0,b2,...,bn-1 of a lattice L (can be given by its Gram matrix), we transform the vector
 * bi such as they form a LLL-reduced basis, we also provide a coordinates matrix H that can map the LLL-reduced terms to
 * the original basis.
 *
 * /!\ The original algorithm use one-indexed, we provide a zero-indexed which means you need to subtract 1 to all k at
 *  the beginning in the original paper.
 *
 * We will adopt the following convention bi* are the GSO vectors, Bi are their norms, mu are the normalized coefficients,
 * each loop has both bounds included, we assume we used the basis and not the Gram Matrix, delta is between 1/2 and 1,
 * if there is more than 1 parameter in the parenthesis after a variable we try to access row first element, column
 * second element, the letters followed by a variable means the vector/element at that position, if it becomes too hard
 * to follow, squared brackets will be used
 *
 *
 * The steps are at the numbers of 4:
 *
 * Step 1: Initialization
 *  k:=1
 *  kmax:=0
 *  b0*:=b0
 *  B0:=b0.b0
 *  H:=Identity_matrix(n,n)
 *
 * Step 2: Incremental GS
 *  if k{@literal <}=kmax then go to Step 3
 *  else
 *      kmax:=k
 *      bk*:=bk
 *      for j:=0...k-1 do
 *          mu:=(bk.bk*)/Bj
 *          bk*:=bk*-mu(k,j).bj*
 *      Bk:=bk*.bk*
 *      if Bk=0 then throw Error, bi not a basis;
 *
 * Step 3: LLL condition
 *  RED(k,k-1) // size reduce the vector bk
 *  if Bk{@literal <} (delta - mu(k,k-1)^2).B[k-1] then
 *      SWAP(k) // swap vector k and k-1
 *      k:=max(1,k-1); // clamp value to 1 at worse
 *  else
 *      for l=k-1...0
 *          RED(k,l)
 *      k:=k-1
 *
 * Step 4: Finish
 *  if k{@literal <}n then go to Step 2
 *  else
 *      output b0...bn-1 and H0...Hn-1
 *
 *
 * The RED and SWAP algorithm are pretty straight forward (we ignore dependent vector for SWAP)
 *
 * RED(k,l)
 *  if |mu(k,l)|{@literal <}=0.5 then return // (this is due to the rounding later)
 *  else
 *      q=round(mu(k,l))
 *      bk:=bk-q.bl
 *      Hk:=Hk-qHl
 *      mu(k,l):=mu(k,l)-q
 *      for i=0...l-1
 *          mu(k,i):=mu(k,i)-q.mu(l,i)
 * SWAP(k)
 *  bk,b[k-1],Hk,H[k-1]:=b[k-1],bk,H[k-1],Hk
 *  if k{@literal >}1 then
 *      for j:=0...k-2
 *          mu(k,j),mu(k-1,j):=mu(k-1,j),mu(k,j)
 *  tmu:=mu(k,k-1)
 *  tB:=Bk+tmu^2.B[k-1]
 *  // here if dependent vectors:
 *  if B=0 then Bk:=B[k-1];B[k-1]:=0;for i:k+1...kmax do mu(i,k):=mu(i,k-1);mu(i,k-1):=0 done;
 *  // if Bk=0 and tmu!=0 B[k-1]:=tB; mu(k,k-1):=1/tmu; for i:k+1...kmax do mu(i,k-1):=mu(i,k-1)/tmu done;
 *  // else do following
 *  t:=B[k-1]/tB
 *  mu(k,k-1):=tmu.t
 *  Bk:= Bk.t
 *  B[k-1]:=tB
 *  for i:=k+1...kmax
 *      t:=mu(i,k)
 *      mu(i,k):=mu(i,k-1)-tmu.t
 *      mu(i,k-1):=t+mu(k,k-1).mu(i,k)
 *
 */
// @formatter:on

public class LLL {
    private final int nbRows;
    private final int nbCols;
    private final BigMatrix coordinates; // also knows as H, give coordinates of the LLL-reduced basis in terms of the initial one
    // GSO stands for Gram-Schmidt Orthogonalization
    private BigMatrix baseGSO; // this is the Gram-Schmidt (almost Orthogonal thus GSO) basis
    private BigMatrix mu; // those are the mu that are used in the Gram-Schimdt process
    private BigVector norms; // those are simply the magnitude of each GS vectors
    private BigMatrix basis; // also knows as the lattice, it's the internal representation so its easier to have it named like that
    private Params params;

    public LLL(BigMatrix lattice, Params params) {
        this.basis = lattice.copy();
        this.nbRows = lattice.getRowCount();
        this.nbCols = lattice.getColumnCount();
        this.baseGSO = new BigMatrix(this.nbRows, this.nbCols);
        this.mu = new BigMatrix(this.nbRows, this.nbRows);
        this.norms = new BigVector(this.nbRows);
        this.coordinates = BigMatrix.identityMatrix(this.nbRows);
        this.params = params;
    }

    /**
     * LLL lattice reduction implemented as described on page 95 of Henri Cohen's
     * "A course in computational number theory"
     *
     * @param lattice the lattice to reduce
     * @param params  the parameters to be passed to LLL
     * @return the reduced lattice
     */
    public static Result reduce(BigMatrix lattice, Params params) {
        return new LLL(lattice, params).reduceLLL(lattice);
    }

    public static Result reduce(BigMatrix lattice) {
        return new LLL(lattice, new Params()).reduceLLL(lattice);
    }

    public void setParams(Params params) {
        this.params = params;
    }

    private boolean testCondition(int k, BigFraction delta) {
        BigFraction muTemp = mu.get(k, k - 1);
        BigFraction factor = delta.subtract(muTemp.multiply(muTemp));
        return norms.get(k).compareTo(norms.get(k - 1).multiply(factor)) < 0;
    }

    private void updateGSO(int k) {
        BigVector newRow = basis.getRow(k).copy();
        for (int j = 0; j <= k - 1; j++) {
            if (!norms.get(j).equals(BigFraction.ZERO)) {
                mu.set(k, j, basis.getRow(k).dot(baseGSO.getRow(j)).divide(norms.get(j)));
            } else {
                mu.set(k, j, BigFraction.ZERO);
            }
            newRow.subtractAndSet(baseGSO.getRow(j).multiply(mu.get(k, j)));
        }
        baseGSO.setRow(k, newRow);
        norms.set(k, newRow.magnitudeSq());
        //if (norms.get(k).equals(BigFraction.ZERO)) {System.err.print("The bi's did not form a basis\n");}
    }

    private void red(int i, int j) {
        BigInteger r = mu.get(i, j).round();
        if (r.equals(BigInteger.ZERO)) { // case |mu(i,j)|<1/2 since it is rounded towards zero that's exactly that
            return;
        }
        basis.getRow(i).subtractAndSet(basis.getRow(j).multiply(r));
        coordinates.getRow(i).subtractAndSet(coordinates.getRow(j).multiply(r));
        mu.set(i, j, mu.get(i, j).subtract(r));
        for (int col = 0; col <= j - 1; col++) {
            mu.set(i, col, mu.get(i, col).subtract(mu.get(j, col).multiply(r)));
        }
    }

    private void swapg(int k, int kmax) {
        basis.swapRowsAndSet(k, k - 1);
        coordinates.swapRowsAndSet(k, k - 1);
        if (k > 1) {
            for (int j = 0; j <= k - 2; j++) {
                mu.swapElementsAndSet(k, j, k - 1, j);
            }
        }
        BigFraction tmu = mu.get(k, k - 1);
        BigFraction tB = norms.get(k).add(tmu.multiply(tmu).multiply(norms.get(k - 1)));
        if (tB.equals(BigFraction.ZERO)) {
            norms.set(k, norms.get(k - 1));
            norms.set(k - 1, BigFraction.ZERO);
            baseGSO.swapRowsAndSet(k, k - 1);
            for (int i = k + 1; i <= kmax; i++) {
                mu.set(i, k, mu.get(i, k - 1));
                mu.set(i, k - 1, BigFraction.ZERO);
            }
        } else if (norms.get(k).equals(BigFraction.ZERO) && !tmu.equals(BigFraction.ZERO)) {
            norms.set(k - 1, tB);
            baseGSO.getRow(k - 1).multiplyAndSet(tmu);
            mu.set(k, k - 1, BigFraction.ONE.divide(tmu));
            for (int i = k + 1; i <= kmax; i++) {
                mu.set(i, k - 1, mu.get(i, k - 1).divide(tmu));
            }
        } else {
            BigFraction t = norms.get(k - 1).divide(tB);
            mu.set(k, k - 1, tmu.multiply(t));
            BigVector b = baseGSO.getRow(k - 1).copy();
            baseGSO.setRow(k - 1, baseGSO.getRow(k).add(b.multiply(tmu)));
            baseGSO.setRow(k, (b.multiply(norms.get(k).divide(tB)).subtract(baseGSO.getRow(k).multiply(mu.get(k, k - 1)))));
            norms.set(k, norms.get(k).multiply(t));
            norms.set(k - 1, tB);
            for (int i = k + 1; i <= kmax; i++) {
                t = mu.get(i, k);
                mu.set(i, k, mu.get(i, k - 1).subtract(tmu.multiply(t)));
                mu.set(i, k - 1, t.add(mu.get(k, k - 1).multiply(mu.get(i, k))));
            }
        }
    }

    private int removeZeroes() {
        int p = 0;
        for (int i = 0; i < nbRows; i++) {
            if (basis.getRow(i).isZero()) {
                p++;
            }
        }
        basis = basis.submatrix(p, 0, nbRows - p, nbCols);
        baseGSO = baseGSO.submatrix(p, 0, nbRows - p, nbCols);
        mu = mu.submatrix(p, p, nbRows - p, nbRows - p);
        BigVector nonZeroQNorms = new BigVector(nbRows - p);
        for (int i = 0; i < nbRows - p; i++) {
            nonZeroQNorms.set(i, norms.get(i + p));
        }
        norms = nonZeroQNorms;
        return p;
    }

    public Result reduceLLL(BigMatrix lattice) {
        this.basis = lattice.copy();
        int k = 1;
        int kmax = 0;
        int n = params.maxStage == -1 ? nbRows : params.maxStage;
        baseGSO.setRow(0, basis.getRow(0));
        boolean updateGSO = true;
        norms.set(0, basis.getRow(0).magnitudeSq());
        while (k < n) {
            if (k > kmax && updateGSO) {
                kmax = k;
                updateGSO(k);
            }
            red(k, k - 1);
            if (testCondition(k, this.params.delta)) {
                swapg(k, kmax); // we update the GSO in here
                k = Math.max(1, k - 1);
                updateGSO = false;
            } else {
                for (int l = k - 2; l >= 0; l--) {
                    red(k, l);
                }
                k++;
                updateGSO = true;
            }
        }
        int p = removeZeroes();
        return new Result(p, basis, coordinates).setGramSchmidtInfo(baseGSO, mu, norms);
    }

}
