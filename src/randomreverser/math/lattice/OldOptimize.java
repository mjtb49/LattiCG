package randomreverser.math.lattice;

import randomreverser.math.component.BigFraction;
import randomreverser.math.component.BigMatrix;
import randomreverser.math.component.BigVector;

import java.util.Arrays;

/**
 * A table-based Simplex solver made specifically for the type of linear optimization needed by Enumerate. It's package
 * private since it should only be used by Enumerate, but it's too much code to put in a static inner class.
 */
class OldOptimize {
    /*
     * Perform a single pivot operation on the table.
     */
    private static void pivot(BigMatrix table, int[] B, int[] N, int variables, int constraints, int entering, int exiting) {
        int rows = table.getRowCount();
        int cols = table.getColumnCount();

        int a = rows - constraints;
        int b = cols - 1;

        assert 0 <= entering && entering < b;
        assert 0 <= exiting && exiting < constraints;

        BigFraction pivot = table.get(a + exiting, entering);

        for (int col = 0; col < cols; ++col) {
            if (col == entering) {
                continue;
            }

            // A[exitingRow, col] /= A[exitingRow, enteringCol
            table.set(a + exiting, col, table.get(a + exiting, col).divide(pivot));
        }

        for (int row = 0; row < rows; ++row) {
            if (row == a + exiting) {
                continue;
            }

            BigFraction x = table.get(row, entering);

            for (int col = 0; col < cols; ++col) {
                if (col == entering) {
                    continue;
                }

                BigFraction y = table.get(a + exiting, col);

                // A[row, col] -= A[row, enteringCol] * A[exitingRow, col]
                table.set(row, col, table.get(row, col).subtract(x.multiply(y)));
            }

            table.set(row, entering, x.divide(pivot).negate());
        }

        table.set(a + exiting, entering, table.get(a + exiting, entering).reciprocal());

        // clearly superior way:
        // N[entering] ^= B[exiting] ^= N[entering];

        int temp = N[entering];
        N[entering] = B[exiting];
        B[exiting] = temp;
    }

    /*
     * Perform a single step on the table. Return false if the table is already optimal.
     */
    private static boolean step(BigMatrix table, int[] B, int[] N, int variables, int constraints) {
        int rows = table.getRowCount();
        int cols = table.getColumnCount();

        // offset to first constraint in the table
        int a = rows - constraints;

        // offset to last column of table
        int b = cols - 1;

        boolean bland = false;

        int entering = -1;
        int exiting = -1;

        BigFraction candidate = BigFraction.ZERO;

        for (int row = 0; row < constraints; ++row) {
            if (table.get(a + row, b).equals(BigFraction.ZERO)) {
                bland = true;
                break;
            }
        }

        for (int col = 0; col < b; ++col) {
            BigFraction x = table.get(0, col);

            if (x.signum() > 0 && (entering == -1 || x.compareTo(candidate) > 0)) {
                entering = col;
                candidate = x;

                if (bland) {
                    break;
                }
            }
        }

        if (entering == -1) {
            return false;
        }

        for (int row = 0; row < constraints; ++row) {
            BigFraction x = table.get(a + row, entering);

            if (x.signum() > 0) {
                BigFraction y = table.get(a + row, b).divide(x);

                if (exiting == -1 || y.compareTo(candidate) < 0) {
                    exiting = row;
                    candidate = y;
                }
            }
        }

        pivot(table, B, N, variables, constraints, entering, exiting);

        return true;
    }

    public static BigVector optimize(BigMatrix initialTable, int size, int depth) {
        BigMatrix table = new BigMatrix(2 + size + depth, size + 1);

        // copy over initial table
        for (int row = 1; row < 2 + size + depth; ++row) {
            for (int col = 0; col < size + 1; ++col) {
                table.set(row, col, initialTable.get(row - 1, col));
            }
        }

        // initialize the objective function for the pre-optimization
        for (int row = 2 + size; row < 2 + size + depth; ++row) {
            table.getRow(0).addEquals(table.getRow(row));
        }

        int[] B = new int[size + depth];
        int[] N = new int[size];

        for (int i = 0; i < size; ++i) {
            N[i] = i;
        }

        for (int i = 0; i < size + depth; ++i) {
            B[i] = size + i;
        }

        // pre-optimize (find a valid initial basis)
        while (step(table, B, N, 2 * size + depth, size + depth)) {
            // step does the hard work
        }

        // swap out fake variables
        for (int row = 0; row < size + depth; ++row) {
            if (B[row] >= 2 * size) {
                assert table.get(2 + row, size).equals(BigFraction.ZERO);

                for (int col = 0; col < size; ++col) {
                    if (N[col] < 2 * size && !table.get(2 + row, col).equals(BigFraction.ZERO)) {
                        pivot(table, B, N, 2 * size + depth, size + depth, col, row);
                        break;
                    }
                }
            }
        }

        initialTable = table;
        table = new BigMatrix(1 + size + depth, size - depth + 1);

        // move the non-basic real variables to the new table
        for (int c0 = 0, c1 = 0; c0 < size - depth; ++c0, ++c1) {
            for (;; ++c1) {
                assert c1 < size;

                if (N[c1] < 2 * size) {
                    for (int row = 0; row < 1 + size + depth; ++row) {
                        table.set(row, c0, initialTable.get(1 + row, c1));
                    }

                    N[c0] = N[c1];

                    break;
                }
            }
        }

        N = Arrays.copyOf(N, size - depth);

        for (int row = 0; row < 1 + size + depth; ++row) {
            table.set(row, size - depth, initialTable.get(1 + row, size));
        }


        while (step(table, B, N, 2 * size, size + depth)) {
            // step does the hard work
        }

        // copy answer out of table
        BigVector result = new BigVector(size);

        for (int i = 0; i < size + depth; ++i) {
            if (B[i] < size) {
                result.set(B[i], table.get(1 + i, size - depth));
            }
        }

        return result;
    }
}
