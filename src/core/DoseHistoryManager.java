package core;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import UI.*;

public class DoseHistoryManager {
    private static final String FILE_PATH = "data/dose_history.txt";

    // Ensure the CSV file exists; create if not.
    public static void ensureCSVExists() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Adds a dose history entry to the CSV file.
    public static void addDoseHistory(DoseHistory doseHistory) {
        ensureCSVExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(doseHistory.toCSV());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retrieves the dose history for a specific user.
    public static List<DoseHistory> getDoseHistoryByUser(String username) {
        ensureCSVExists();
        List<DoseHistory> historyList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                DoseHistory history = DoseHistory.fromCSV(line);
                if (history != null && history.getUsername().equals(username)) {
                    historyList.add(history);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return historyList;
    }

    // Displays all dose history for a specific user with Scheduled Time and Taken Time.
    public static void displayDoseHistoryByUser(String username) {
        UI.clearScreen();
        UI.printBoxedTitle("DOSE HISTORY OF " + username);

        List<DoseHistory> history = getDoseHistoryByUser(username);
        if (history.isEmpty()) {
            System.out.println("No dose history found");
            return;
        }

        List<String> headers = List.of("Medicine", "Scheduled Time", "Taken Time");
        List<List<String>> rows = history.stream()
                .map(h -> List.of(
                        h.getMedicineName(),
                        h.getScheduledTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        h.getTakenTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                ))
                .collect(Collectors.toList());

        UI.displayReminderTable(headers, rows);
    }
}
