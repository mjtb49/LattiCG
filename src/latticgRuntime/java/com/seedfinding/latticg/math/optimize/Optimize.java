package com.seedfinding.latticg.math.optimize;

import com.seedfinding.latticg.math.component.BigFraction;
import com.seedfinding.latticg.math.component.BigMatrix;
import com.seedfinding.latticg.math.component.BigVector;
import com.seedfinding.latticg.math.component.GaussJordan;
import com.seedfinding.latticg.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Optimize {
    private final BigMatrix transform;
    private final BigMatrix table;
    private final int[] basics;
    private final int[] nonbasics;

    // these are purely here to make the code less cluttered with calls to
    // getRowCount() and getColCount()
    private final int rows;
    private final int cols;

    private Optimize(BigMatrix table, int[] basics, int[] nonbasics, BigMatrix transform) {
        this.table = table;
        this.basics = basics;
        this.nonbasics = nonbasics;
        this.transform = transform;

        this.rows = this.table.getRowCount();
        this.cols = this.table.getColumnCount();
    }

    private BigVector transformForTable(BigVector lhs, BigFraction rhs) {
        BigVector transformed = new BigVector(this.transform.getColumnCount());
        BigVector eliminated = new BigVector(this.cols);

        transformed.set(this.transform.getColumnCount() - 1, rhs);

        for (int row = 0; row < this.transform.getRowCount(); ++row) {
            BigFraction x = lhs.get(row);
            transformed.subtractAndSet(this.transform.getRow(row).multiply(x));
        }

        for (int col = 0; col < this.cols - 1; ++col) {
            eliminated.set(col, transformed.get(this.nonbasics[col]));
        }

        eliminated.set(this.cols - 1, transformed.get(this.transform.getColumnCount() - 1));

        for (int row = 0; row < this.rows - 1; ++row) {
            BigFraction x = transformed.get(this.basics[row]);
            eliminated.subtractAndSet(this.table.getRow(row).multiply(x));
        }

        return eliminated;
    }

    public Pair<BigVector, BigFraction> maximize(BigVector gradient) {
        Pair<BigVector, BigFraction> result = this.minimize(gradient.multiply(BigFraction.MINUS_ONE));
        return new Pair<>(result.getFirst(), result.getSecond().negate());
    }

    public Pair<BigVector, BigFraction> minimize(BigVector gradient) {
        if (gradient.getDimension() != this.transform.getRowCount()) {
            throw new IllegalArgumentException("invalid size of gradient");
        }

        this.table.setRow(this.rows - 1, new BigVector(this.cols));
        this.table.getRow(this.rows - 1).subtractAndSet(this.transformForTable(gradient, BigFraction.ZERO));

        this.solve();

        BigVector result = this.transform.getColumn(this.transform.getColumnCount() - 1).copy();

        for (int row = 0; row < this.rows - 1; ++row) {
            int v0 = this.basics[row];
            result.subtractAndSet(this.transform.getColumn(v0).multiply(this.table.get(row, this.cols - 1)));
        }

        return new Pair<>(result, this.table.get(this.rows - 1, this.cols - 1));
    }

    private void solve() {
        while (this.step()) {
            // step does the hard work
        }
    }

    private boolean step() {
        boolean bland = false;

        int entering = -1;
        int exiting = -1;

        BigFraction candidate = BigFraction.ZERO;

        for (int row = 0; row < this.rows - 1; ++row) {
            if (this.table.get(row, this.cols - 1).signum() == 0) {
                bland = true;
                break;
            }
        }

        for (int col = 0; col < this.cols - 1; ++col) {
            BigFraction x = this.table.get(this.rows - 1, col);

            if (x.signum() <= 0 || (entering != -1 && x.compareTo(candidate) <= 0)) {
                continue;
            }

            entering = col;
            candidate = x;

            if (bland) {
                break;
            }
        }

        if (entering == -1) {
            return false;
        }

        for (int row = 0; row < this.rows - 1; ++row) {
            BigFraction x = this.table.get(row, entering);

            if (x.signum() <= 0) {
                continue;
            }

            BigFraction y = this.table.get(row, this.cols - 1).divide(x);

            if (exiting != -1 && y.compareTo(candidate) >= 0) {
                continue;
            }

            exiting = row;
            candidate = y;
        }

        this.pivot(entering, exiting);
        return true;
    }

    private void pivot(int entering, int exiting) {
        int rows = this.table.getRowCount();
        int cols = this.table.getColumnCount();

        int constraints = rows - 1;
        int variables = cols - 1;

        assert 0 <= entering && entering < variables;
        assert 0 <= exiting && exiting < constraints;

        BigFraction pivot = this.table.get(exiting, entering);

        for (int col = 0; col < cols; ++col) {
            if (col == entering) {
                continue;
            }

            this.table.set(exiting, col, this.table.get(exiting, col).divide(pivot));
        }

        for (int row = 0; row < rows; ++row) {
            if (row == exiting) {
                continue;
            }

            BigFraction x = this.table.get(row, entering);

            for (int col = 0; col < cols; ++col) {
                if (col == entering) {
                    continue;
                }

                BigFraction y = table.get(exiting, col);

                this.table.set(row, col, this.table.get(row, col).subtract(x.multiply(y)));
            }

            this.table.set(row, entering, x.divide(pivot).negate());
        }

        this.table.set(exiting, entering, pivot.reciprocal());

        int temp = this.nonbasics[entering];
        this.nonbasics[entering] = this.basics[exiting];
        this.basics[exiting] = temp;
    }

    public Optimize copy() {
        return new Optimize(this.table.copy(), Arrays.copyOf(this.basics, this.rows - 1), Arrays.copyOf(this.nonbasics, this.cols - 1), this.transform);
    }

    public Optimize withStrictBound(BigVector lhs, BigFraction rhs) {
        BigMatrix newTable = new BigMatrix(this.rows + 1, this.cols);

        for (int row = 0; row < this.rows - 1; ++row) {
            newTable.setRow(row, this.table.getRow(row));
        }

        newTable.setRow(this.rows - 1, this.transformForTable(lhs, rhs));

        if (newTable.get(this.rows - 1, this.cols - 1).signum() < 0) {
            newTable.getRow(this.rows - 1).multiplyAndSet(BigFraction.MINUS_ONE);
        }

        int[] newBasics = Arrays.copyOf(this.basics, this.rows);
        int[] newNonbasics = Arrays.copyOf(this.nonbasics, this.cols - 1);
        newBasics[this.rows - 1] = (this.rows - 1) + (this.cols - 1);

        return from(newTable, newBasics, newNonbasics, 1, this.transform);
    }

    private static Optimize from(BigMatrix table, int[] basics, int[] nonbasics, int artificials, BigMatrix transform) {
        int rows = table.getRowCount();
        int cols = table.getColumnCount();

        int realVariables = (rows - 1) + (cols - 1) - artificials;

        for (int basicRow = 0; basicRow < rows - 1; ++basicRow) {
            if (basics[basicRow] < realVariables) {
                continue;
            }

            table.getRow(rows - 1).addAndSet(table.getRow(basicRow));
        }

        Optimize optimize = new Optimize(table, basics, nonbasics, null);
        optimize.solve();

        if (table.get(rows - 1, cols - 1).signum() != 0) {
            throw new IllegalArgumentException("table has no basic feasible solutions: " + table.get(rows - 1, cols - 1));
        }

        for (int row = 0; row < rows - 1; ++row) {
            if (basics[row] >= realVariables) {
                for (int col = 0; col < cols - 1; ++col) {
                    if (nonbasics[col] >= realVariables || table.get(row, col).signum() == 0) {
                        continue;
                    }

                    optimize.pivot(col, row);
                    break;
                }
            }
        }

        int finalCols = cols - artificials;
        BigMatrix finalTable = new BigMatrix(rows, finalCols);

        for (int c0 = 0, c1 = 0; c0 < finalCols - 1; ++c0, ++c1) {
            for (; ; ++c1) {
                if (nonbasics[c1] >= realVariables) {
                    continue;
                }

                for (int row = 0; row < rows - 1; ++row) {
                    finalTable.set(row, c0, table.get(row, c1));
                }

                nonbasics[c0] = nonbasics[c1];
                break;
            }
        }

        for (int row = 0; row < rows - 1; ++row) {
            finalTable.set(row, finalCols - 1, table.get(row, cols - 1));
        }

        return new Optimize(finalTable, basics, nonbasics, transform);
    }

    private static Optimize from(BigMatrix innerTable, BigMatrix transform) {
        int constraints = innerTable.getRowCount();
        int variables = innerTable.getColumnCount() - 1;

        int[] basics = new int[constraints]; // row -> variable
        Arrays.fill(basics, -1);

        List<Integer> nonbasicList = new ArrayList<>(); // col (in final table) -> variable

        for (int row = 0; row < constraints; ++row) {
            if (innerTable.get(row, variables).signum() >= 0) {
                continue;
            }

            innerTable.getRow(row).multiplyAndSet(BigFraction.MINUS_ONE);
        }

        for (int col = 0, i = 0; col < variables; ++col) {
            int count = 0;
            int index = -1;

            for (int row = 0; row < innerTable.getRowCount(); ++row) {
                if (innerTable.get(row, col).signum() == 0) {
                    continue;
                }

                count++;
                index = row;
            }

            if (count == 1 && basics[index] == -1 && innerTable.get(index, col).signum() > 0) {
                innerTable.getRow(index).divideAndSet(innerTable.get(index, col));
                basics[index] = col;
            } else {
                nonbasicList.add(col);
            }
        }

        int artificials = 0;

        for (int row = 0; row < constraints; ++row) {
            if (basics[row] != -1) {
                continue;
            }

            basics[row] = variables + artificials;
            artificials += 1;
        }

        int[] nonbasics = nonbasicList.stream().mapToInt(i -> i).toArray();
        int nonbasicCount = variables - constraints + artificials;
        BigMatrix table = new BigMatrix(constraints + 1, nonbasicCount + 1);

        for (int row = 0; row < constraints; ++row) {
            for (int basicRow = 0; basicRow < constraints; ++basicRow) {
                if (row == basicRow || basics[basicRow] >= variables) {
                    continue;
                }

                BigVector rowVector = innerTable.getRow(row);
                BigVector basicVector = innerTable.getRow(basicRow);

                rowVector.subtractAndSet(basicVector.multiply(rowVector.get(basics[basicRow])));
            }

            for (int col = 0; col < nonbasicCount; ++col) {
                table.set(row, col, innerTable.get(row, nonbasics[col]));
            }

            table.set(row, nonbasicCount, innerTable.get(row, variables));
        }

        return from(table, basics, nonbasics, artificials, transform);
    }

    public static class Builder {
        private final int size;
        private final List<Integer> slacks;
        private final List<BigVector> lefts;
        private final List<BigFraction> rights;

        private Builder(int size) {
            this.size = size;
            this.slacks = new ArrayList<>();
            this.lefts = new ArrayList<>();
            this.rights = new ArrayList<>();
        }

        public static Builder ofSize(int size) {
            return new Builder(size);
        }

        private void checkLHS(BigVector lhs) {
            if (lhs.getDimension() != this.size) {
                throw new IllegalArgumentException("invalid size of lhs: " + lhs.getDimension());
            }
        }

        public Optimize build() {
            int variables = this.size + this.slacks.size();
            int constraint = 0;
            int slack = this.size;

            // make enough room for at most (size) many extra slack constraints
            // and at most (2 * size) many extra slack variables, plus the rhs
            BigMatrix table = new BigMatrix(this.slacks.size() + this.size, variables + 2 * this.size + 1);

            // fill in the table with the constraint we've collected
            for (; constraint < this.slacks.size(); ++constraint) {
                for (int col = 0; col < this.size; ++col) {
                    table.set(constraint, col, this.lefts.get(constraint).get(col));
                }

                table.set(constraint, variables + 2 * this.size, this.rights.get(constraint));

                if (this.slacks.get(constraint) != 0) {
                    table.set(constraint, slack, new BigFraction(this.slacks.get(constraint)));
                    slack += 1;
                }
            }

            // reduce the real variables out of the matrix (since they don't have a sign constraint)
            int[] pivotRows = GaussJordan.reduce(table, (col, rows) -> col < this.size);

            // for any real variables we couldn't remove, add a slack pair
            for (int col = 0; col < this.size; ++col) {
                if (pivotRows[col] != -1) {
                    continue;
                }

                table.getRow(constraint).set(col, BigFraction.ONE);
                table.getRow(constraint).set(slack, BigFraction.ONE);
                table.getRow(constraint).set(slack + 1, BigFraction.MINUS_ONE);

                constraint += 1;
                slack += 2;
            }

            // re-reduce the whole table so we can remove the rest of the real
            // variables as well as any linearly dependent variables
            pivotRows = GaussJordan.reduce(table);

            // TODO: test and remove
            // this doesn't need to be here so long as reduce works (and the
            // logic in the rest of this function is correct), but it's gonna
            // stay until this is well tested
            for (int col = 0; col < this.size; ++col) {
                if (pivotRows[col] != -1) {
                    continue;
                }

                throw new IllegalStateException("something went wrong? couldn't remove column from table");
            }

            // remove linearly dependent variables
            constraint = 1 + Arrays.stream(pivotRows).max().orElse(-1);

            // now the matrix looks like
            //
            //      I | T | 0 | t
            //      -------------
            //      0 | A | 0 | b
            //      -------------
            //      0 | 0 | 0 | 0
            //
            // which means that
            //
            //      x = t - T * s
            //
            // where x is the vector of real variables and s is the vector of
            // slack variables

            BigMatrix transform = new BigMatrix(this.size, slack - this.size + 1);
            BigMatrix innerTable = new BigMatrix(constraint - this.size, slack - this.size + 1);

            for (int row = 0; row < this.size; ++row) {
                for (int col = 0; col < slack - this.size; ++col) {
                    transform.set(row, col, table.get(row, this.size + col));
                }

                transform.set(row, slack - this.size, table.get(row, variables + 2 * this.size));
            }

            for (int row = 0; row < constraint - this.size; ++row) {
                for (int col = 0; col < slack - this.size; ++col) {
                    innerTable.set(row, col, table.get(this.size + row, this.size + col));
                }

                innerTable.set(row, slack - this.size, table.get(this.size + row, variables + 2 * this.size));
            }

            return from(innerTable, transform);
        }

        private void checkLHS(int lhs) {
            if (!(0 <= lhs && lhs < this.size)) {
                throw new IllegalArgumentException("invalid index of lhs: " + lhs);
            }
        }

        private void add(int slack, BigVector lhs, BigFraction rhs) {
            this.slacks.add(slack);
            this.lefts.add(lhs);
            this.rights.add(rhs);
        }

        public Builder withLowerBound(BigVector lhs, BigFraction rhs) {
            this.checkLHS(lhs);
            this.add(-1, lhs.copy(), rhs);

            return this;
        }

        public Builder withUpperBound(BigVector lhs, BigFraction rhs) {
            this.checkLHS(lhs);
            this.add(1, lhs.copy(), rhs);

            return this;
        }

        public Builder withStrictBound(BigVector lhs, BigFraction rhs) {
            this.checkLHS(lhs);
            this.add(0, lhs.copy(), rhs);

            return this;
        }

        public Builder withLowerBound(int lhs, BigFraction rhs) {
            this.checkLHS(lhs);
            this.add(-1, BigVector.basis(this.size, lhs), rhs);

            return this;
        }

        public Builder withUpperBound(int lhs, BigFraction rhs) {
            this.checkLHS(lhs);
            this.add(1, BigVector.basis(this.size, lhs), rhs);

            return this;
        }

        public Builder withLowerBound(int lhs, long rhs) {
            return withLowerBound(lhs, new BigFraction(rhs));
        }

        public Builder withUpperBound(int lhs, long rhs) {
            return withUpperBound(lhs, new BigFraction(rhs));
        }

        public Builder withStrictBound(int lhs, BigFraction rhs) {
            this.checkLHS(lhs);
            this.add(0, BigVector.basis(this.size, lhs), rhs);

            return this;
        }
    }
}
