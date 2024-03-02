package com.seedfinding.latticg.math.component;

import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

@ApiStatus.Internal
public class GaussJordan {
    private GaussJordan() { }

    private static void forAll(BigMatrix matrix, Collection<BigMatrix> others, Consumer<BigMatrix> action) {
        action.accept(matrix);
        others.forEach(action);
    }

    public static int[] reduce(BigMatrix matrix, Collection<BigMatrix> others, ReduceColumnPredicate reduceColumn) {
        int[] pivotRows = new int[matrix.getColumnCount()];
        Arrays.fill(pivotRows, -1);

        int row = 0;
        int pivotColumn = 0;

        while (row < matrix.getRowCount() && pivotColumn < matrix.getColumnCount()) {
            int pivotRow;

            for (pivotRow = row; pivotRow < matrix.getRowCount(); ++pivotRow) {
                if (!matrix.get(pivotRow, pivotColumn).equals(BigFraction.ZERO)) {
                    break;
                }
            }

            if (pivotRow < matrix.getRowCount()) {
                final int finalRow = row;
                final int finalPivotRow = pivotRow;
                final int finalPivotColumn = pivotColumn;

                final BigFraction finalPivot = matrix.get(finalPivotRow, finalPivotColumn);

                forAll(matrix, others, m -> m.getRow(finalPivotRow).divideAndSet(finalPivot));

                for (int i = 0; i < matrix.getRowCount(); ++i) {
                    if (i == finalPivotRow) {
                        continue;
                    }

                    final int finalI = i;
                    final BigFraction finalScale = matrix.get(i, finalPivotColumn);

                    forAll(matrix, others, m -> m.getRow(finalI).subtractAndSet(m.getRow(finalPivotRow).multiply(finalScale)));
                }

                forAll(matrix, others, m -> m.swapRowsAndSet(finalRow, finalPivotRow));
                pivotRows[finalPivotColumn] = finalRow;
                ++row;
            }

            do {
                ++pivotColumn;
            } while (pivotColumn < matrix.getColumnCount() && !reduceColumn.test(pivotColumn, pivotRows));
        }

        return pivotRows;
    }

    public static int[] reduce(BigMatrix matrix, BigMatrix other, ReduceColumnPredicate reduceColumn) {
        return reduce(matrix, Collections.singleton(other), reduceColumn);
    }

    public static int[] reduce(BigMatrix matrix, ReduceColumnPredicate reduceColumn) {
        return reduce(matrix, Collections.emptyList(), reduceColumn);
    }

    public static int[] reduce(BigMatrix matrix, Collection<BigMatrix> others) {
        return reduce(matrix, others, ReduceColumnPredicate.ALWAYS);
    }

    public static int[] reduce(BigMatrix matrix, BigMatrix other) {
        return reduce(matrix, Collections.singleton(other), ReduceColumnPredicate.ALWAYS);
    }

    public static int[] reduce(BigMatrix matrix) {
        return reduce(matrix, Collections.emptyList(), ReduceColumnPredicate.ALWAYS);
    }

    @FunctionalInterface
    public interface ReduceColumnPredicate {
        ReduceColumnPredicate ALWAYS = (pivotColumn, pivotRows) -> true;

        boolean test(int pivotColumn, int[] pivotRows);
    }
}
