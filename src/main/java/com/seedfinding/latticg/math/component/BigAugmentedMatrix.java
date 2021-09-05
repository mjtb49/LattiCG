package com.seedfinding.latticg.math.component;

import com.seedfinding.latticg.util.StringUtils;

public class BigAugmentedMatrix {

    private final BigMatrix base;
    private final BigMatrix extra;

    public BigAugmentedMatrix(BigMatrix base, BigMatrix extra) {
        this.base = base;
        this.extra = extra;
    }

    public BigMatrix getBase() {
        return this.base;
    }

    public BigMatrix getExtra() {
        return this.extra;
    }

    public void divideRow(int y, BigFraction scalar) {
        this.base.getRow(y).divideAndSet(scalar);
        this.extra.getRow(y).divideAndSet(scalar);
    }

    public void subtractScaledRow(int y1, BigFraction scalar, int y2) {
        this.base.getRow(y1).subtractAndSet(this.base.getRow(y2).multiply(scalar));
        this.extra.getRow(y1).subtractAndSet(this.extra.getRow(y2).multiply(scalar));
    }

    @Override
    public String toString() {
        return StringUtils.tableToString(Math.max(base.getRowCount(), extra.getRowCount()), base.getColumnCount() + extra.getColumnCount(), (row, column) -> {
            if (column < base.getColumnCount()) {
                if (row >= base.getRowCount()) {
                    return "";
                } else {
                    return base.get(row, column).toString();
                }
            } else {
                column -= base.getColumnCount();
                if (row >= extra.getRowCount()) {
                    return "";
                } else {
                    return extra.get(row, column).toString();
                }
            }
        }, (row, column) -> {
            if (column == 0) {
                return "[";
            } else if (column == base.getColumnCount()) {
                return "|";
            } else if (column == base.getColumnCount() + extra.getColumnCount()) {
                return "]";
            } else {
                return " ";
            }
        });
    }

}
