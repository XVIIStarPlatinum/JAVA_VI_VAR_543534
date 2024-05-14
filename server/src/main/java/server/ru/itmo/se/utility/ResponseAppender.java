package server.ru.itmo.se.utility;

/**
 * Class used for appending colored and formatted strings as a response.
 */
public class ResponseAppender {
    /**
     * This field is used to accumulate formatted strings.
     */
    public static StringBuilder output = new StringBuilder();

    /**
     * This method is a custom implementation of the print() method. The output is colored purple with the Help of a corresponding ANSI code.
     * @param toOut the object to be printed.
     */
    public static void append(Object toOut) {
        output.append(toOut);
    }

    /**
     * This method is a custom implementation of the println() method. The output is colored green with the Help of a corresponding ANSI code.
     */
    public static void appendln() {
        output.append("\n");
    }

    /**
     *
     * @param toOut the object to be printed.
     */
    public static void appendln(Object toOut) {
        output.append(toOut).append("\n");
    }
    /**
     * This method is a custom implementation of the err.print() method. The output is colored black with red background with the Help of corresponding ANSI codes.
     * @param toOut the error that was raised.
     */
    public static void appendError(Object toOut) {
        output.append("Error: ").append(toOut).append("\n");
    }

    /**
     * This method is used to output all the commands in a table format. The row border is colored purple, the object and the column border is colored cyan with the Help of corresponding ANSI codes.
     * @param e1 first column object.
     * @param e2 second column object.
     */
    public static void appendTable(Object e1, Object e2) {
        output.append(String.format("| %-64s | %-75s | %n", e1, e2)).append("=-".repeat(73)).append("\n");
    }

    /**
     * This method is used to retrieve the accumulated string.
     * @return resulting string.
     */
    public static String getString() {
        return output.toString();
    }

    /**
     * This method is used to retrieve the accumulated string and to clear the buffer.
     * @return resulting string.
     */
    public static String getAndClear() {
        String toReturn = getString();
        output.delete(0, output.length());
        return toReturn;
    }

    /**
     * This method is used to clear the StringBuilder buffer.
     */
    public static void clear() {
        output.delete(0, output.length());
    }
}
