package fi.evident.cissa.utils;

public final class HexUtils {

    private static final String digits = "0123456789abcdef";

    /**
     * Produces hexadecimal string from a sequence of numbers, each of
     * which is taken to represent a number between 0..255.
    */
    public static String hexify(int... bytes) {
        StringBuilder sb = new StringBuilder();
        for (int b : bytes) {
            sb.append(digits.charAt((b >> 4) & 15));
            sb.append(digits.charAt(b & 15));
        }
        return sb.toString();
    }
}
