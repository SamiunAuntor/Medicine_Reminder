package UI;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class UI {
    private static Scanner scanner = new Scanner(System.in);

    public static void displayReminderTable(List<String> headers, List<List<String>> rows) {
        if (headers == null || rows == null || headers.isEmpty() || rows.isEmpty()) {
            System.out.println("No data to display");
            return;
        }

        int[] widths = new int[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            widths[i] = headers.get(i).length();
        }
        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                widths[i] = Math.max(widths[i], row.get(i).length());
            }
        }

        int computedContentWidth = Arrays.stream(widths).sum() + 2 * widths.length + (widths.length - 1);

        int tableWidth = Math.max(50, computedContentWidth);

        String topBorder = buildBorder("╔", "╗", tableWidth);
        String headerSeparator = buildBorder("╠", "╣", tableWidth);
        String rowSeparator = buildBorder("╟", "╢", tableWidth);
        String bottomBorder = buildBorder("╚", "╝", tableWidth);

        System.out.println(topBorder);
        printRow("║", headers, widths, tableWidth);
        System.out.println(headerSeparator);
        for (int i = 0; i < rows.size(); i++) {
            printRow("║", rows.get(i), widths, tableWidth);
            if (i < rows.size() - 1) {
                System.out.println(rowSeparator);
            }
        }
        System.out.println(bottomBorder);
    }

    private static String buildBorder(String left, String right, int width) {
        return left + "═".repeat(width) + right;
    }

    private static void printRow(String edge, List<String> cells, int[] widths, int tableWidth) {
        // Build content for the row: each cell as " " + left-aligned content + " "
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < cells.size(); i++) {
            contentBuilder.append(" ")
                    .append(String.format("%-" + widths[i] + "s", cells.get(i)))
                    .append(" ");
            if (i < cells.size() - 1) {
                contentBuilder.append("│");
            }
        }
        String content = contentBuilder.toString();
        // Center the content in a field of tableWidth
        String centeredContent = centerText(content, tableWidth);
        // Build the row with vertical edges
        System.out.println(edge + centeredContent + edge);
    }

    public static void printBoxedMenu(String[] options, String title) {
        int maxLength = title.length();
        for (String option : options) {
            maxLength = Math.max(maxLength, option.length() + 4);
        }
        int boxWidth = Math.max(50, maxLength + 4);
        String horizontalBorder = "═".repeat(boxWidth);

        System.out.println("╔" + horizontalBorder + "╗");

        String centeredTitle = centerText(title, boxWidth);
        System.out.println("║" + centeredTitle + "║");

        System.out.println("║" + " ".repeat(boxWidth) + "║");

        for (int i = 0; i < options.length; i++) {
            String optionText = String.format("%d. %s", i + 1, options[i]);
            String paddedOption = String.format("%-" + boxWidth + "s", " " + optionText);
            System.out.println("║" + paddedOption + "║");

            if (i < options.length - 1) {
                System.out.println("║" + " ".repeat(boxWidth) + "║");
            }
        }

        System.out.println("║" + " ".repeat(boxWidth) + "║");
        System.out.println("╚" + horizontalBorder + "╝");
        System.out.println();

        System.out.print("Select an option: ");
    }

    public static void printBoxedTitle(String title) {
        int boxWidth = Math.max(50, title.length() + 4);
        String horizontalBorder = "═".repeat(boxWidth);

        System.out.println("╔" + horizontalBorder + "╗");
        String centeredTitle = centerText(title, boxWidth);
        System.out.println("║" + centeredTitle + "║");
        System.out.println("╚" + horizontalBorder + "╝");
        System.out.println();
    }

    public static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }


    public static void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    public static void waitForEnter() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();  // Wait for Enter key
        try {
            Thread.sleep(50);  // Half-second pause after Enter
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}