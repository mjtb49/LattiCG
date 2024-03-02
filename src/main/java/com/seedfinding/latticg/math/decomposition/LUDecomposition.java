package com.seedfinding.latticg.math.decomposition;

import com.seedfinding.latticg.math.component.BigFraction;
import com.seedfinding.latticg.math.component.BigMatrix;
import com.seedfinding.latticg.math.component.BigMatrixUtil;
import com.seedfinding.latticg.math.component.BigVector;
import com.seedfinding.latticg.math.component.Matrix;
import com.seedfinding.latticg.math.component.Vector;

import java.util.regex.Pattern;

public class LUDecomposition {

    public static Result decompose(Matrix matrix) {
        if (!matrix.isSquare()) {
            throw new UnsupportedOperationException("Matrix is not square");
        }

        Matrix m = matrix.copy();
        int size = m.getRowCount();
        Vector p = new Vector(size);
        Matrix inv = Matrix.identityMatrix(size);
        int swaps = 0;

        //Decomposition
        for (int i = 0; i < size; i++) {
            int pivot = -1;
            double beegestNumbor = 0.0D;

            for (int row = i; row < size; row++) {
                double d = Math.abs(m.get(row, i));

                if (d > beegestNumbor) {
                    beegestNumbor = d;
                    pivot = row;
                }
            }

            if (pivot == -1) {
                throw new IllegalStateException("Matrix is singular");
            }

            p.set(i, pivot);
            inv.swapRowsAndSet(i, pivot);

            if (pivot != i) {
                m.swapRowsAndSet(i, pivot);
                swaps++;
            }

            for (int row = i + 1; row < size; row++) {
                m.set(row, i, m.get(row, i) / m.get(i, i));
            }

            for (int row = i + 1; row < size; row++) {
                for (int col = i + 1; col < size; col++) {
                    m.set(row, col, m.get(row, col) - m.get(row, i) * m.get(i, col));
                }
            }
        }

        //Determinant
        double det = 1.0D;

        for (int i = 0; i < size; i++) {
            det *= m.get(i, i);
        }

        det *= (swaps & 1) == 0 ? 1 : -1;

        //Inverse
        for (int dcol = 0; dcol < size; dcol++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < row; col++) {
                    inv.set(row, dcol, inv.get(row, dcol) - m.get(row, col) * inv.get(col, dcol));
                }
            }
        }

        for (int dcol = 0; dcol < size; dcol++) {
            for (int row = size - 1; row >= 0; row--) {
                for (int col = size - 1; col > row; col--) {
                    inv.set(row, dcol, inv.get(row, dcol) - m.get(row, col) * inv.get(col, dcol));
                }

                inv.set(row, dcol, inv.get(row, dcol) / m.get(row, row));
            }
        }

        return new Result(m, p, det, inv);
    }

    public static BigResult decompose(BigMatrix matrix) {
        if (!matrix.isSquare()) {
            throw new UnsupportedOperationException("Matrix is not square");
        }

        BigMatrix m = matrix.copy();
        int size = m.getRowCount();
        BigVector p = new BigVector(size);
        BigMatrix inv = BigMatrix.identityMatrix(size);
        int swaps = 0;

        //Decomposition
        for (int i = 0; i < size; i++) {
            int pivot = -1;
            BigFraction beegestNumbor = BigFraction.ZERO;

            for (int row = i; row < size; row++) {
                BigFraction d = m.get(row, i).abs();

                if (d.compareTo(beegestNumbor) > 0) {
                    beegestNumbor = d;
                    pivot = row;
                }
            }

            if (pivot == -1) {
                throw new IllegalStateException("Matrix is singular");
            }

            p.set(i, new BigFraction(pivot));
            inv.swapRowsAndSet(i, pivot);

            if (pivot != i) {
                m.swapRowsAndSet(i, pivot);
                swaps++;
            }

            for (int row = i + 1; row < size; row++) {
                m.set(row, i, m.get(row, i).divide(m.get(i, i)));
            }

            for (int row = i + 1; row < size; row++) {
                for (int col = i + 1; col < size; col++) {
                    m.set(row, col, m.get(row, col).subtract(m.get(row, i).multiply(m.get(i, col))));
                }
            }
        }

        //Determinant
        BigFraction det = BigFraction.ONE;

        for (int i = 0; i < size; i++) {
            det = det.multiply(m.get(i, i));
        }

        if ((swaps & 1) != 0) det.negate();

        //Inverse
        for (int dcol = 0; dcol < size; dcol++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < row; col++) {
                    inv.set(row, dcol, inv.get(row, dcol).subtract(m.get(row, col).multiply(inv.get(col, dcol))));
                }
            }
        }

        for (int dcol = 0; dcol < size; dcol++) {
            for (int row = size - 1; row >= 0; row--) {
                for (int col = size - 1; col > row; col--) {
                    inv.set(row, dcol, inv.get(row, dcol).subtract(m.get(row, col).multiply(inv.get(col, dcol))));
                }

                inv.set(row, dcol, inv.get(row, dcol).divide(m.get(row, row)));
            }
        }

        return new BigResult(m, p, det, inv);
    }

    public static final class Result {

        private final int size;
        private final Matrix P;
        private final Matrix L;
        private final Matrix U;
        private final double det;
        private final Matrix inv;

        private Result(Matrix lu, Vector p, double det, Matrix inv) {
            this.size = lu.getRowCount();

            this.L = new Matrix(this.size, this.size, (row, col) -> {
                if (row > col) return lu.get(row, col);
                else if (row == col) return 1.0D;
                else return 0.0D;
            });

            this.U = new Matrix(this.size, this.size, (row, col) -> {
                if (row <= col) return lu.get(row, col);
                else return 0.0D;
            });

            this.P = Matrix.identityMatrix(this.size);

            for (int i = 0; i < this.size; i++) {
                this.P.swapRowsAndSet(i, (int) p.get(i));
            }

            this.det = det;
            this.inv = inv;
        }

        public int getMatrixSize() {
            return this.size;
        }

        public Matrix getP() {
            return this.P;
        }

        public Matrix getL() {
            return this.L;
        }

        public Matrix getU() {
            return this.U;
        }

        public double getDet() {
            return this.det;
        }

        public Matrix inverse() {
            return this.inv;
        }

        public String toPrettyString() {
            StringBuilder sb = new StringBuilder();
            String[] uStuff = this.U.toPrettyString().split(Pattern.quote("\n"));
            String[] lStuff = this.L.toPrettyString().split(Pattern.quote("\n"));
            String[] pStuff = this.P.toPrettyString().split(Pattern.quote("\n"));

            for (int i = 0; i < lStuff.length; i++) {
                sb.append(lStuff[i]).append("  ").append(uStuff[i]).append("  ").append(pStuff[i]);
                if (i != lStuff.length - 1) sb.append("\n");
            }

            return sb.toString();
        }

        @Override
        public String toString() {
            return this.P + " | " + this.L + " | " + this.U;
        }

    }

    public static final class BigResult {

        private final int size;
        private final BigMatrix P;
        private final BigMatrix L;
        private final BigMatrix U;
        private final BigFraction det;
        private final BigMatrix inv;

        private BigResult(BigMatrix lu, BigVector p, BigFraction det, BigMatrix inv) {
            this.size = lu.getRowCount();

            this.L = new BigMatrix(this.size, this.size, (row, col) -> {
                if (row > col) return lu.get(row, col);
                else if (row == col) return BigFraction.ONE;
                else return BigFraction.ZERO;
            });

            this.U = new BigMatrix(this.size, this.size, (row, col) -> {
                if (row <= col) return lu.get(row, col);
                else return BigFraction.ZERO;
            });

            this.P = BigMatrix.identityMatrix(this.size);

            for (int i = 0; i < this.size; i++) {
                this.P.swapRowsAndSet(i, p.get(i).getNumerator().intValue());
            }

            this.det = det;
            this.inv = inv;
        }

        public int getMatrixSize() {
            return this.size;
        }

        public BigMatrix getP() {
            return this.P;
        }

        public BigMatrix getL() {
            return this.L;
        }

        public BigMatrix getU() {
            return this.U;
        }

        public BigFraction getDet() {
            return this.det;
        }

        public BigMatrix inverse() {
            return this.inv;
        }

        public String toPrettyString() {
            StringBuilder sb = new StringBuilder();
            String[] uStuff = BigMatrixUtil.toPrettyString(this.U).split(Pattern.quote("\n"));
            String[] lStuff = BigMatrixUtil.toPrettyString(this.L).split(Pattern.quote("\n"));
            String[] pStuff = BigMatrixUtil.toPrettyString(this.P).split(Pattern.quote("\n"));

            for (int i = 0; i < lStuff.length; i++) {
                sb.append(lStuff[i]).append("  ").append(uStuff[i]).append("  ").append(pStuff[i]);
                if (i != lStuff.length - 1) sb.append("\n");
            }

            return sb.toString();
        }

        @Override
        public String toString() {
            return this.P + " | " + this.L + " | " + this.U;
        }

    }
}
