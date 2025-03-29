package core;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NotificationManager {
    private static final String FILE_PATH = "D:\\SWE\\3rd Semester\\SWE 4302 OOP II Lab (Group A)\\OOP II Project\\Medicine Reminder\\notifications.txt";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Method to create the notifications file if it doesn't exist
    private static void createNotificationsFileIfNotExist() {
        File file = new File(FILE_PATH);
        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("Created new file: notifications.txt");
                } else {
                    System.out.println("Failed to create the file: notifications.txt");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Store notification when medicine time comes
    public static void addNotification(String username, String medicineName, LocalTime time) {
        createNotificationsFileIfNotExist(); // Ensure the file exists before adding

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(username + "," + medicineName + "," + time.format(TIME_FORMATTER) + "," + "PENDING");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Display notifications in table format
    public static void showNotification(String username, Scanner scanner) {
        createNotificationsFileIfNotExist(); // Ensure the file exists before reading

        List<String> notifications = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            int index = 1;

            System.out.printf("%-5s %-20s %-10s %-10s\n", "No.", "Medicine", "Time", "Status");
            System.out.println("------------------------------------------------------");

            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(username) && details[3].equals("PENDING")) {
                    System.out.printf("%-5d %-20s %-10s %-10s\n", index, details[1], details[2], details[3]);
                    notifications.add(line);
                    index++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (notifications.isEmpty()) {
            System.out.println("NO PENDING NOTIFICATIONS");
            return;
        }

        System.out.println("Select a notification number to process or enter 0 to go back:");
        int choice = scanner.nextInt();
        scanner.nextLine();
        if (choice > 0 && choice <= notifications.size()) {
            processNotification(notifications.get(choice - 1), scanner);
        }
    }

    // Process selected notification
    private static void processNotification(String notification, Scanner scanner) {
        System.out.println("1. Medicine Taken\n2. Medicine Skipped\n3. Back");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                updateNotificationStatus(notification, "TAKEN");
                break;
            case 2:
                updateNotificationStatus(notification, "SKIPPED");
                break;
            case 3:
                return; // Go back without making any changes
            default:
                System.out.println("Invalid choice. Try again.");
                processNotification(notification, scanner); // Recurse to ask again
        }
    }

    // Update notification status in file
    private static void updateNotificationStatus(String notification, String status) {
        List<String> allNotifications = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(notification)) {
                    String[] details = line.split(",");
                    details[3] = status; // Update the status to "TAKEN" or "SKIPPED"
                    allNotifications.add(String.join(",", details));
                } else {
                    allNotifications.add(line); // Keep unchanged notifications
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write all notifications back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String entry : allNotifications) {
                writer.write(entry);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Notification updated successfully.");
    }
}
