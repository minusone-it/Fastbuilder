package it.minusone.fastbuilder.utils.utils.bytes;

public class NumberConversion {

    public static String toBinary(byte i) {
        return toBinary(i, 8);
    }

    public static String toBinary(long i, int size) {
        return toBinaryString(i, size, false);
    }

    public static String toSizedBinary(long i, int size) {
        return toBinaryString(i, size, true);
    }

    public static String toBinaryString(long i, int size, boolean actualSize) {
        boolean negative = i < 0;
        if (negative)
            i = -i - 1;
        StringBuilder s = new StringBuilder();
        int j = 0;
        while (negative || actualSize ? j < size : i != 0) {
            s.insert(0, negative ? 1 - i % 2 : i % 2);
            i >>>= 1;
            j++;
        }
        return s.toString();
    }

    public static String toHex(long i, int size) {
        return toHexString(i, size, false);
    }

    public static String toSizedHex(long i, int size) {
        return toHexString(i, size, true);
    }


    public static String toHexString(long i, int size, boolean actualSize) {
        boolean negative = i < 0;
        if (negative)
            i = -i - 1;
        StringBuilder s = new StringBuilder();
        long j = 0, t;
        while (negative || actualSize ? j < size : i != 0) {
            t = negative ? 15 - i % 16 : i % 16;
            s.insert(0, (char) (t < 10 ? t + 48 : t + 55));
            i >>>= 4;
            j++;
        }
        return s.toString();
    }


    public static byte[] toByteArray(long i, int size) {
        size /= 8;
        byte[] b = new byte[size];
        for (int j = 0; j < size; j++)
            b[j] = (byte) (i >>> (size - j - 1) * 8 & 0xFF);
        return b;
    }

}
