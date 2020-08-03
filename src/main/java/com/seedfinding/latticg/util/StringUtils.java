package com.seedfinding.latticg.util;

public class StringUtils {

    public static String tableToString(int rows, int columns, TableCellFunction<String> cellExtractor) {
        return tableToString(rows, columns, cellExtractor, (row, column) -> {
            if (column == 0) {
                return "[";
            } else if (column == columns) {
                return "]";
            } else {
                return " ";
            }
        });
    }

    public static String tableToString(int rows, int columns, TableCellFunction<String> cellExtractor, TableCellFunction<String> separator) {
        StringBuilder[][] parts = new StringBuilder[columns * 2 + 1][rows];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                parts[column * 2][row] = new StringBuilder(separator.get(row, column));
                parts[column * 2 + 1][row] = new StringBuilder(cellExtractor.get(row, column));
            }
            parts[columns * 2][row] = new StringBuilder(separator.get(row, columns));
        }
        for (StringBuilder[] column : parts) {
            int columnWidth = 0;
            for (StringBuilder cell : column) {
                if (cell.length() > columnWidth) {
                    columnWidth = cell.length();
                }
            }
            for (StringBuilder cell : column) {
                int whitespace = columnWidth - cell.length();
                for (int i = 0, e = whitespace / 2; i < e; i++) {
                    cell.insert(0, " ");
                }
                for (int i = 0, e = (whitespace + 1) / 2; i < e; i++) {
                    cell.insert(0, " ");
                }
            }
        }
        StringBuilder finalStr = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            if (row != 0) {
                finalStr.append("\n");
            }
            for (StringBuilder[] column : parts) {
                finalStr.append(column[row]);
            }
        }
        return finalStr.toString();
    }

    @FunctionalInterface
    public interface TableCellFunction<T> {
        T get(int row, int column);
    }

}
