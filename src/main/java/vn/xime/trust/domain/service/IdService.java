package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.Id;

import java.util.Arrays;

public final class IdService {

    private static final char[] BASE62 =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private IdService() {}

    // =========================
    // DEBUG VIEW
    // =========================

    public static String toBase62(Id id) {
        return encodeBase62(id.toBytes());
    }

    public static String toHex(Id id) {
        byte[] bytes = id.toBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static String toByteString(Id id) {
        return Arrays.toString(id.toBytes());
    }

    // =========================
    // BASE62 (internal)
    // =========================

    private static String encodeBase62(byte[] input) {
        byte[] copy = Arrays.copyOf(input, input.length);
        StringBuilder sb = new StringBuilder();

        while (!isZero(copy)) {
            int remainder = divmod(copy, 62);
            sb.append(BASE62[remainder]);
        }

        return sb.reverse().toString();
    }

    private static int divmod(byte[] number, int divisor) {
        int remainder = 0;

        for (int i = 0; i < number.length; i++) {
            int digit = number[i] & 0xFF;
            int temp = remainder * 256 + digit;

            number[i] = (byte) (temp / divisor);
            remainder = temp % divisor;
        }

        return remainder;
    }

    private static boolean isZero(byte[] number) {
        for (byte b : number) {
            if (b != 0) return false;
        }
        return true;
    }
}