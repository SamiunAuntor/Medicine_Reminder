package core;

import java.io.*;
import java.util.*;

public class DoseHistoryManager {
    private static final String FILE_PATH = "data/dose_history.txt";

    // Ensure the CSV file exists; if not, create it
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

    // Add a dose history entry to the CSV file
    public static void addDoseHistory(DoseHistory doseHistory) {
        ensureCSVExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(doseHistory.toCSV());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get the dose history for a specific user
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

    // Get the dose history for a specific medicine of a specific user
    public static List<DoseHistory> getDoseHistoryByMedicine(String username, String medicineName) {
        ensureCSVExists();
        List<DoseHistory> historyList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                DoseHistory history = DoseHistory.fromCSV(line);
                if (history != null && history.getUsername().equals(username) && history.getMedicineName().equals(medicineName)) {
                    historyList.add(history);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return historyList;
    }

    // Display all dose history for a specific user
    public static void displayDoseHistoryByUser(String username) {
        List<DoseHistory> historyList = getDoseHistoryByUser(username);
        if (historyList.isEmpty()) {
            System.out.println("No dose history found for user: " + username);
            return;
        }

        System.out.println("Dose History for user: " + username);
        for (DoseHistory history : historyList) {
            System.out.println("Medicine: " + history.getMedicineName() + " | Dose Time: " + history.getDoseTime());
        }
    }

    // Display dose history for a specific medicine of a user
    public static void displayDoseHistoryByMedicine(String username, String medicineName) {
        List<DoseHistory> historyList = getDoseHistoryByMedicine(username, medicineName);
        if (historyList.isEmpty()) {
            System.out.println("No dose history found for medicine: " + medicineName + " for user: " + username);
            return;
        }

        System.out.println("Dose History for " + medicineName + " for user: " + username);
        for (DoseHistory history : historyList) {
            System.out.println("Dose Time: " + history.getDoseTime());
        }
    }
}
