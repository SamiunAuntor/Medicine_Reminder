package UI;

import java.util.List;

public class UI {
    public static void displayReminderTable(List<String> headers, List<List<String>> rows) {
        if (headers == null || rows == null || headers.isEmpty() || rows.isEmpty()) {
            System.out.println("No data to display");
            return;
        }

        // Calculate column widths
        int[] widths = new int[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            widths[i] = headers.get(i).length();
        }

        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                widths[i] = Math.max(widths[i], row.get(i).length());
            }
        }

        // Build borders
        String topBorder = buildBorder("╔", "╦", "╗", widths);
        String headerSeparator = buildBorder("╠", "╬", "╣", widths);
        String rowSeparator = buildBorder("╟", "╫", "╢", widths);
        String bottomBorder = buildBorder("╚", "╩", "╝", widths);

        // Print table
        System.out.println(topBorder);
        printRow("║", headers, widths);
        System.out.println(headerSeparator);

        for (int i = 0; i < rows.size(); i++) {
            printRow("║", rows.get(i), widths);
            if (i < rows.size() - 1) {
                System.out.println(rowSeparator);
            }
        }

        System.out.println(bottomBorder);
    }

    private static String buildBorder(String left, String middle, String right, int[] widths) {
        StringBuilder border = new StringBuilder(left);
        for (int i = 0; i < widths.length; i++) {
            border.append("═".repeat(widths[i] + 2));
            if (i < widths.length - 1) {
                border.append(middle);
            }
        }
        border.append(right);
        return border.toString();
    }

    private static void printRow(String edge, List<String> cells, int[] widths) {
        StringBuilder row = new StringBuilder(edge);
        for (int i = 0; i < cells.size(); i++) {
            row.append(" ").append(String.format("%-" + widths[i] + "s", cells.get(i))).append(" ").append(edge);
        }
        System.out.println(row);
    }
}