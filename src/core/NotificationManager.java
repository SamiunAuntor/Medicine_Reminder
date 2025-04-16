package core;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import UI.*;

public class NotificationManager {

    private static final Scanner scanner = new Scanner(System.in);

    public static void displayMissedDoses(String username) {
        UI.clearScreen();
        UI.printBoxedTitle("MISSED DOSES for " + username);

        List<String> headers = List.of("Medicine", "Time", "Date");
        List<List<String>> rows = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        List<Medicine> medicines = Medicine.getUserMedicines(username);
        for (Medicine med : medicines) {
            List<Reminder> reminders = Reminder.getRemindersByMedicine(username, med.getName());
            for (Reminder rem : reminders) {
                LocalDateTime reminderDateTime = LocalDateTime.of(rem.getDate(), rem.getTime());
                if (reminderDateTime.isBefore(now) && !rem.isTaken()) {
                    rows.add(List.of(
                            med.getName(),
                            rem.getTime().toString(),
                            rem.getDate().toString()
                    ));
                }
            }
        }
        if (rows.isEmpty()) {
            System.out.println("No missed doses found.");
        } else {
            UI.displayReminderTable(headers, rows);
        }
    }

    public static void displayMedicineTimeNotifications(String username) {
        UI.clearScreen();
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                    NOTIFICATION PANEL                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        List<Notification> notifications = Notification.getUserNotifications(username)
                .stream()
                .filter(n -> n.getType() == NotificationType.MEDICINE_TIME && !n.isProcessed())
                .collect(Collectors.toList());

        if (notifications.isEmpty()) {
            System.out.println("╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                 No notifications found.                  ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            return;
        }

        List<String> headers = List.of("#", "Type", "Message", "Status");
        List<List<String>> rows = new ArrayList<>();
        for (int i = 0; i < notifications.size(); i++) {
            Notification n = notifications.get(i);
            rows.add(List.of(
                    String.valueOf(i + 1),
                    n.getType().toString(),
                    n.getMessage(),
                    n.isProcessed() ? "Processed" : "Pending"
            ));
        }

        UI.displayReminderTable(headers, rows);
        processSelectedNotification(notifications);
    }

    private static void processSelectedNotification(List<Notification> notifications) {
        System.out.print("Enter notification number to process (0 to cancel): ");
        int choice = readIntInput();
        if (choice > 0 && choice <= notifications.size()) {
            Notification selected = notifications.get(choice - 1);
            processNotification(selected);
        }
    }

    public static void processNotification(Notification notification) {
        UI.clearScreen();
        if (notification.getType() == NotificationType.MEDICINE_TIME) {
            handleMedicineTimeNotification(notification);
        }
        markNotificationAsProcessed(notification.getUsername(), notification.getMessage());
    }

    private static void handleMedicineTimeNotification(Notification notification) {
        String[] parts = notification.getMessage().split(" due at ");
        if (parts.length != 2) {
            System.out.println("Invalid notification format");
            return;
        }

        String medicineName = parts[0].trim();
        String dateTimeStr = parts[1].trim();

        try {
            LocalDateTime dueTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String title = String.format("Did you take '%s' at %s?",
                    medicineName,
                    dueTime.format(DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd")));
            String[] options = { "Mark as taken", "Mark as not taken" };
            UI.printBoxedMenu(options, title);
            int choice = readIntInput();

            if (choice == 1) {
                // Mark as taken: update reminder, record dose history, update stock.
                Reminder.markReminderAsTaken(notification.getUsername(), medicineName,
                        dueTime.toLocalDate(), dueTime.toLocalTime());
                DoseHistoryManager.addDoseHistory(new DoseHistory(
                        notification.getUsername(), medicineName, dueTime, LocalDateTime.now()));
                updateMedicineStock(notification.getUsername(), medicineName, -1);
                System.out.println("Dose marked as taken. Dose history updated.");
            } else if (choice == 2) {
                System.out.println("Dose marked as not taken. It will appear in Missed Doses.");
            }
            // Mark notification as processed (so it won't appear again).
            markNotificationAsProcessed(notification.getUsername(), notification.getMessage());
        } catch (Exception e) {
            System.out.println("Error parsing date/time: " + e.getMessage());
        }
    }


    private static void updateMedicineStock(String username, String medicineName, int quantityChange) {
        List<Medicine> medicines = Medicine.getUserMedicines(username);
        for (Medicine medicine : medicines) {
            if (medicine.getName().equals(medicineName)) {
                int newQuantity = medicine.getQuantity() + quantityChange;
                if (newQuantity >= 0) {
                    Medicine.updateMedicineStock(username, medicineName, newQuantity);
                    System.out.println("Medicine stock updated: " + medicineName + " now has " + newQuantity + " left.");
                } else {
                    System.out.println("Not enough stock to update.");
                }
                return;
            }
        }
        System.out.println("Medicine not found in your inventory.");
    }

    public static void markNotificationAsProcessed(String username, String message) {
        try {
            List<String> lines = new ArrayList<>(java.nio.file.Files.readAllLines(java.nio.file.Paths.get("data/notifications.txt")));
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/notifications.txt"));

            for (String line : lines) {
                String[] data = line.split(",");
                if (data.length >= 4 && data[0].equals(username) && data[1].equals(message)) {
                    data[3] = "true";  // mark as processed
                    line = String.join(",", data);
                }
                writer.write(line);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int readIntInput() {
        int number = -1;
        while (true) {
            try {
                number = Integer.parseInt(scanner.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid integer: ");
            }
        }
        return number;
    }

    // ✅ MUST-HAVE METHODS: DO NOT TOUCH

    // Duplicate-check version for Medicine Time Notifications
    public static void addMedicineTimeNotification(String username, String message) {
        // Now check among all notifications with the same type and message, regardless of processed status
        boolean exists = Notification.getUserNotifications(username).stream()
                .anyMatch(n -> n.getType() == NotificationType.MEDICINE_TIME
                        && n.getMessage().equals(message));
        if (!exists) {
            Notification notification = new Notification(
                    username,
                    message,
                    NotificationType.MEDICINE_TIME,
                    false
            );
            Notification.addNotification(notification);
        }
    }


    public static void addMissedDoseNotification(String username, String message) {
        Notification missedDoseNotification = new Notification(username, message, NotificationType.MISSED_DOSE, false);
        Notification.addNotification(missedDoseNotification);
    }
}
