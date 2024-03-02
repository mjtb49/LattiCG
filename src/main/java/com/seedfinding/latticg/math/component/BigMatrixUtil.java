package com.seedfinding.latticg.math.component;

import com.seedfinding.latticg.math.decomposition.LUDecomposition;
import com.seedfinding.latticg.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class BigMatrixUtil {
    private BigMatrixUtil() {
    }

    /**
     * Computes the inverse of this matrix, {@code this}<sup>-1</sup>, stores the result in a new matrix and returns
     * that matrix
     *
     * @return A new matrix containing the result
     * @throws UnsupportedOperationException If this is not a square matrix
     * @throws IllegalStateException         If this matrix is singular
     */
    public static BigMatrix inverse(BigMatrix matrix) {
        return LUDecomposition.decompose(matrix).inverse();
    }

    /**
     * Formats this matrix nicely into a human-readable multi-line string
     *
     * @return The formatted matrix
     */
    public static String toPrettyString(BigMatrix matrix) {
        return toPrettyString(matrix, false);
    }

    /**
     * Formats this matrix nicely into a human-readable multi-line string
     *
     * @param approximate a boolean to specify if the result should be converted to double
     * @return The formatted matrix
     */
    public static String toPrettyString(BigMatrix matrix, boolean approximate) {
        return StringUtils.tableToString(matrix.getRowCount(), matrix.getColumnCount(), (row, column) -> approximate ? String.valueOf(matrix.get(row, column).toDouble()) : matrix.get(row, column).toString());
    }

    public static BigMatrix fromString(String str) {
        str = str.trim();
        if (!str.startsWith("{") || !str.endsWith("}")) {
            throw new IllegalArgumentException("Illegal BigMatrix format");
        }
        List<BigVector> rows = new ArrayList<>();
        int vectorEnd;
        for (int vectorStart = str.indexOf('{', 1); vectorStart >= 0; vectorStart = str.indexOf('{', vectorEnd + 1)) {
            vectorEnd = str.indexOf('}', vectorStart + 1);
            rows.add(BigVectorUtil.fromString(str.substring(vectorStart, vectorEnd + 1)));
        }

        if (rows.isEmpty()) {
            return new BigMatrix(0, 0);
        }

        BigMatrix matrix = new BigMatrix(rows.size(), rows.get(0).getDimension());
        for (int i = 0; i < rows.size(); i++) {
            matrix.setRow(i, rows.get(i));
        }
        return matrix;
    }
}
