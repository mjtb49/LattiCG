package com.seedfinding.latticg.generator;

import com.seedfinding.latticg.math.component.BigFraction;
import com.seedfinding.latticg.math.component.BigMatrix;
import com.seedfinding.latticg.math.component.BigVector;

import java.awt.event.KeyEvent;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

public final class SerializeUtil {
    private SerializeUtil() {
    }

    public static String matrixToStringLiteral(String indent, BigMatrix matrix) {
        ByteVector buf = new ByteVector();
        writeBigMatrix(buf, matrix);
        return bufToStringLiteral(indent, buf);
    }

    private static void writeBigMatrix(ByteVector buf, BigMatrix matrix) {
        writeVarInt(buf, matrix.getRowCount());
        if (matrix.getRowCount() == 0) {
            return;
        }
        writeVarInt(buf, matrix.getColumnCount());

        for (int i = 0; i < matrix.getRowCount(); i++) {
            writeBigVector(buf, matrix.getRow(i), false);
        }
    }

    public static String vectorToStringLiteral(String indent, BigVector vector) {
        ByteVector buf = new ByteVector();
        writeBigVector(buf, vector, true);
        return bufToStringLiteral(indent, buf);
    }

    private static void writeBigVector(ByteVector buf, BigVector vector, boolean includeLength) {
        if (includeLength) {
            writeVarInt(buf, vector.getDimension());
        }
        for (int i = 0; i < vector.getDimension(); i++) {
            writeBigFraction(buf, vector.get(i));
        }
    }

    public static String fractionToStringLiteral(String indent, BigFraction fraction) {
        ByteVector buf = new ByteVector();
        writeBigFraction(buf, fraction);
        return bufToStringLiteral(indent, buf);
    }

    private static void writeBigFraction(ByteVector buf, BigFraction fraction) {
        writeBigInt(buf, fraction.getNumerator());
        writeBigInt(buf, fraction.getDenominator());
    }

    public static String bigIntToStringLiteral(String indent, BigInteger value) {
        ByteVector buf = new ByteVector();
        writeBigInt(buf, value);
        return bufToStringLiteral(indent, buf);
    }

    private static void writeBigInt(ByteVector buf, BigInteger value) {
        if (value.signum() == -1) {
            value = value.negate().shiftLeft(1).setBit(0);
        } else {
            value = value.shiftLeft(1);
        }

        do {
            byte b = (byte) (value.intValue() & 0x7F);
            value = value.shiftRight(7);
            if (value.signum() != 0) {
                b |= (byte) 0x80;
            }
            buf.add(b);
        } while (value.signum() != 0);
    }

    private static void writeVarInt(ByteVector buf, int value) {
        do {
            byte b = (byte) (value & 0x7F);
            value >>>= 7;
            if (value != 0) {
                b |= (byte) 0x80;
            }
            buf.add(b);
        } while (value != 0);
    }

    private static String bufToStringLiteral(String indent, ByteVector buf) {
        StringBuilder result = new StringBuilder(indent).append("\"");

        int lineStartIndex = indent.length();

        CharBuffer chars = ByteBuffer.wrap(buf.toEvenLengthArray()).asCharBuffer();
        boolean justPrintedOctal = false;
        while (chars.hasRemaining()) {
            if (result.length() - lineStartIndex >= 126) {
                result.append("\" +\n").append(indent);
                lineStartIndex = result.length();
                result.append("\"");
            }

            char ch = chars.get();
            switch (ch) {
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                case '"':
                    result.append("\\\"");
                    break;
                case '\\':
                    result.append("\\\\");
                    break;
                case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7':
                    if (justPrintedOctal) {
                        result.append('\\').append(Integer.toOctalString(ch));
                        continue;
                    } else {
                        result.append(ch);
                    }
                    break;
                default:
                    if (Character.isISOControl(ch)) {
                        result.append('\\').append(Integer.toOctalString(ch));
                        justPrintedOctal = true;
                        continue;
                    } else if (isPrintable(ch)) {
                        result.append(ch);
                    } else {
                        result.append(String.format("\\u%04x", (int) ch));
                    }
                    break;
            }
            justPrintedOctal = false;
        }

        return result.append("\"").toString();
    }

    private static boolean isPrintable(char ch) {
        switch (Character.getType(ch)) {
            case Character.UPPERCASE_LETTER:
            case Character.LOWERCASE_LETTER:
            case Character.TITLECASE_LETTER:
            case Character.MODIFIER_LETTER:
            case Character.OTHER_LETTER:
            case Character.NON_SPACING_MARK:
            case Character.ENCLOSING_MARK:
            case Character.COMBINING_SPACING_MARK:
            case Character.DECIMAL_DIGIT_NUMBER:
            case Character.LETTER_NUMBER:
            case Character.OTHER_NUMBER:
            case Character.SPACE_SEPARATOR:
            case Character.DASH_PUNCTUATION:
            case Character.START_PUNCTUATION:
            case Character.END_PUNCTUATION:
            case Character.CONNECTOR_PUNCTUATION:
            case Character.OTHER_PUNCTUATION:
            case Character.MATH_SYMBOL:
            case Character.CURRENCY_SYMBOL:
            case Character.MODIFIER_SYMBOL:
            case Character.OTHER_SYMBOL:
            case Character.INITIAL_QUOTE_PUNCTUATION:
            case Character.FINAL_QUOTE_PUNCTUATION:
                return true;
        }
        return false;
    }

    private static final class ByteVector {
        private byte[] array = new byte[16];
        private int size = 0;

        public void add(byte b) {
            if (size >= array.length) {
                array = Arrays.copyOf(array, array.length * 2);
            }
            array[size++] = b;
        }

        public byte[] toEvenLengthArray() {
            return Arrays.copyOf(array, (size + 1) & ~1);
        }
    }
}
