package com.svvorf.texunatest;

/**
 * Static utility functions
 */
public class Utils {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Lengthens a string to a specified length using a specified symbol.
     * @param str a string to pad
     * @param width a desired width
     * @param paddingChar a symbol to use
     * @return padded string
     */
    public static String padToWidth(String str, int width, char paddingChar) {
        StringBuilder stringBuilder = new StringBuilder(str);
        for (int i = str.length(); i < width; i++) {
            stringBuilder.append(paddingChar);
        }

        return stringBuilder.toString();
    }

    /**
     * Lengthens a string to a specified length using spaces.
     * @param str a string to pad
     * @param width a desired width
     * @return padded string
     */
    public static String padToWidth(String str, int width) {
        return padToWidth(str, width, ' ');
    }

    /**
     * @param str a string to count lines of
     * @return the number of lines in a string
     */
    public static int getLinesCount(String str) {
        return str.split(LINE_SEPARATOR).length;
    }
}
