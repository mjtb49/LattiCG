package com.seedfinding.latticg.util;

import com.seedfinding.latticg.math.component.BigFraction;
import com.seedfinding.latticg.math.component.BigMatrix;
import com.seedfinding.latticg.math.component.BigVector;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public final class DeserializeRt {
    private DeserializeRt() {
    }

    public static BigMatrix mat(String str) {
        return readBigMatrix(bufFromString(str));
    }

    private static BigMatrix readBigMatrix(ByteBuffer buf) {
        int numRows = readVarInt(buf);
        if (numRows == 0) {
            return new BigMatrix(0, 0);
        }

        int numCols = readVarInt(buf);
        BigVector[] rows = new BigVector[numRows];
        for (int i = 0; i < numRows; i++) {
            rows[i] = readBigVector(numCols, buf);
        }

        BigMatrix matrix = new BigMatrix(numRows, numCols);
        for (int i = 0; i < numRows; i++) {
            matrix.setRow(i, rows[i]);
        }
        return matrix;
    }

    public static BigVector vec(String str) {
        return readBigVector(bufFromString(str));
    }

    private static BigVector readBigVector(ByteBuffer buf) {
        return readBigVector(readVarInt(buf), buf);
    }

    private static BigVector readBigVector(int len, ByteBuffer buf) {
        BigFraction[] fractions = new BigFraction[len];
        for (int i = 0; i < len; i++) {
            fractions[i] = readBigFraction(buf);
        }
        return new BigVector(fractions);
    }

    public static BigFraction frac(String str) {
        return readBigFraction(bufFromString(str));
    }

    private static BigFraction readBigFraction(ByteBuffer buf) {
        return new BigFraction(readBigInt(buf), readBigInt(buf));
    }

    public static BigInteger scalar(String str) {
        return readBigInt(bufFromString(str));
    }

    private static BigInteger readBigInt(ByteBuffer buf) {
        BigInteger result = BigInteger.ZERO;
        int shift = 0;
        byte b;
        do {
            b = buf.get();
            result = result.or(BigInteger.valueOf(b & 0x7F).shiftLeft(shift));
            shift += 7;
        } while ((b & 0x80) != 0);
        if (result.testBit(0)) {
            result = result.shiftRight(1).negate();
        } else {
            result = result.shiftRight(1);
        }
        return result;
    }

    private static int readVarInt(ByteBuffer buf) {
        int result = 0;
        int shift = 0;
        byte b;
        do {
            if (shift >= 35) {
                throw new IllegalArgumentException("Varint is too long");
            }
            b = buf.get();
            result |= (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        return result;
    }

    private static ByteBuffer bufFromString(String str) {
        ByteBuffer result = ByteBuffer.allocate(str.length() * 2);
        CharBuffer chars = result.asCharBuffer();
        for (int i = 0; i < str.length(); i++) {
            chars.put(str.charAt(i));
        }
        result.limit(chars.position() * 2);
        return result;
    }
}
