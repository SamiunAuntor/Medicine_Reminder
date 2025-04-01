package core;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.time.*;
import java.util.stream.Collectors;

import UI.*;

import static controller.Main.clearScreen;
import static core.Notification.markNotificationAsProcessed;


public class NotificationManager {

    // Process and display all notifications for the user
    // In NotificationManager.java - Update processNotifications
    public static void processNotifications(String username) {
        clearScreen();
        List<Notification> notifications = Notification.getUserNotifications(username);

        System.out.println("=== Notifications ===");
        System.out.println("1. Missed Doses");
        System.out.println("2. Refill Alerts");
        System.out.println("3. Expired Medicines");
        System.out.println("4. Medicine Time Alerts");
        System.out.println("5. Back");
        System.out.print("Choose option: ");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (choice == 5) return;

        NotificationType[] types = {
                NotificationType.MISSED_DOSE,
                NotificationType.REFILL,
                NotificationType.EXPIRED_MEDICINE,
                NotificationType.MEDICINE_TIME
        };

        if (choice < 1 || choice > 4) {
            System.out.println("Invalid choice!");
            return;
        }

        NotificationType selectedType = types[choice-1];
        displayNotificationTable(username, selectedType);
    }

    private static void displayNotificationTable(String username, NotificationType type) {
        List<Notification> notifications = Notification.getUserNotifications(username)
                .stream()
                .filter(n -> n.getType() == type && !n.isProcessed())
                .collect(Collectors.toList());

        if (notifications.isEmpty()) {
            System.out.println("No notifications of this type");
            return;
        }

        List<String> headers = List.of("#", "Type", "Message", "Status");
        List<List<String>> rows = new ArrayList<>();

        for (int i = 0; i < notifications.size(); i++) {
            Notification n = notifications.get(i);
            rows.add(List.of(
                    String.valueOf(i+1),
                    n.getType().toString(),
                    n.getMessage(),
                    n.isProcessed() ? "Processed" : "Pending"
            ));
        }

        UI.displayReminderTable(headers, rows);
        processSelectedNotification(notifications);
    }

    private static void processSelectedNotification(List<Notification> notifications) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter notification number to process (0 to cancel): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= notifications.size()) {
            Notification selected = notifications.get(choice-1);
            processNotification(selected);
        }
    }

    // Process a specific notification
    // In NotificationManager.java - Enhance processNotification
    public static void processNotification(Notification notification) {
        clearScreen();
        switch (notification.getType()) {
            case MISSED_DOSE:
                handleMissedDose(notification);
                break;
            case REFILL:
                handleRefill(notification);
                break;
            case EXPIRED_MEDICINE:
                handleExpiredMedicine(notification);
                break;
            case MEDICINE_TIME:
                handleMedicineTimeNotification(notification);
                break;
        }
        markNotificationAsProcessed(notification.getUsername(), notification.getMessage());
    }

    private static void handleMissedDose(Notification notification) {
        System.out.println("Missed Dose: " + notification.getMessage());
        System.out.println("1. View Missed Dose History");
        System.out.println("2. Mark as Completed");
        System.out.print("Choose option: ");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            showMissedDoses(notification);
        }
        // Marking as completed just processes the notification
    }

    private static void handleRefill(Notification notification) {
        System.out.println("Refill Notification: " + notification.getMessage());
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. Refill Now");
        System.out.println("2. Remind Later");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("Enter quantity to add: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            String medicineName = notification.getMessage().replace("Refill needed for: ", "");
            Medicine.updateMedicineStock(
                    notification.getUsername(),
                    medicineName,
                    // Get current quantity and add new
                    Medicine.getUserMedicines(notification.getUsername()).stream()
                            .filter(m -> m.getName().equals(medicineName))
                            .findFirst()
                            .map(Medicine::getQuantity)
                            .orElse(0) + quantity
            );
            System.out.println(quantity + " units added to " + medicineName);
        }
    }

    private static void handleExpiredMedicine(Notification notification) {
        System.out.println("Expired Medicine: " + notification.getMessage());
        System.out.println("1. Remove from Inventory");
        System.out.println("2. Keep Anyway");
        System.out.print("Choose option: ");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            removeExpiredMedicine(notification.getUsername(), notification.getMessage());
        }
    }

    // Show missed doses notifications
    public static void showMissedDoses(Notification notification) {
        System.out.println("Missed Dose Notification: " + notification.getMessage());
        // The missed doses are stored in a file called "missed_doses.txt"
        String missedDoseFile = "data/missed_doses.txt";
        File file = new File(missedDoseFile);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(missedDoseFile))) {
                String line;
                System.out.println("\nMissed Dose History (Table):");
                System.out.println("----------------------------------------------------");
                System.out.printf("%-15s %-20s %-15s\n", "Username", "Medicine Name", "Missed Date");
                System.out.println("----------------------------------------------------");

                // Read each line and display the missed doses in a table-like format
                while ((line = reader.readLine()) != null) {
                    String[] missedDoseData = line.split(",");
                    String username = missedDoseData[0];
                    String medicineName = missedDoseData[1];
                    String missedDate = missedDoseData[2];

                    // Display each missed dose entry in a formatted table
                    System.out.printf("%-15s %-20s %-15s\n", username, medicineName, missedDate);
                }
                System.out.println("----------------------------------------------------");
            } catch (IOException e) {
                System.out.println("Error reading missed doses file: " + e.getMessage());
            }
        } else {
            System.out.println("No missed doses recorded.");
        }
    }

    // Show refill notifications
    public static void showRefillNotifications(Notification notification) {
        System.out.println("Refill Notification: " + notification.getMessage());
        // Logic to process refill, like adding more stock or prompting user to refill
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to refill now? (Yes/No): ");
        String response = scanner.nextLine();

        if (response.equalsIgnoreCase("Yes")) {
            // Call method to refill medicine
            refillMedicine(notification.getUsername(), notification.getMessage());
        } else {
            System.out.println("You chose to add later.");
        }
    }

    // Show expired medicine notifications
    public static void showExpiredMedicineNotifications(Notification notification) {
        System.out.println("Expired Medicine Notification: " + notification.getMessage());
        // Logic to process expired medicines, like removing from inventory or replacing them
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to remove expired medicine from your stock? (Yes/No): ");
        String response = scanner.nextLine();

        if (response.equalsIgnoreCase("Yes")) {
            removeExpiredMedicine(notification.getUsername(), notification.getMessage());
        } else {
            System.out.println("You chose to keep the expired medicine.");
        }
    }

    // Handle "Time to take your medicine" notifications
    // Modify the medicine time notification handler
    // In NotificationManager.java - Update handleMedicineTimeNotification
    public static void handleMedicineTimeNotification(Notification notification) {
        System.out.println("Medicine Time Notification: " + notification.getMessage());

        // New parsing logic with error handling
        String[] parts = notification.getMessage().split(" due at ");
        if (parts.length != 2) {
            System.out.println("Invalid notification format");
            return;
        }

        String medicineName = parts[0];
        String dateTime = parts[1];

        try {
            LocalDateTime dueTime = LocalDateTime.parse(
                    dateTime,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            );

            Scanner scanner = new Scanner(System.in);
            System.out.printf("Did you take '%s' at %s? (Yes/No): ", medicineName,
                    dueTime.format(DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd")));
            String response = scanner.nextLine();

            if (response.equalsIgnoreCase("Yes")) {
                Reminder.markReminderAsTaken(
                        notification.getUsername(),
                        medicineName,
                        dueTime.toLocalDate(),
                        dueTime.toLocalTime()
                );
                DoseHistoryManager.addDoseHistory(new DoseHistory(
                        notification.getUsername(),
                        medicineName,
                        LocalDateTime.now()
                ));
                updateMedicineStock(notification.getUsername(), medicineName, -1);
            } else {
                recordMissedDose(notification.getUsername(), medicineName);
            }
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing notification datetime");
        }
    }

    // Update the medicine stock when a user marks the medicine as taken
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

    // Record a missed dose in the missed doses file
    private static void recordMissedDose(String username, String medicineName) {
        String missedDoseFile = "data/missed_doses.txt";
        File file = new File(missedDoseFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(missedDoseFile, true))) {
            String missedDoseData = String.format("%s,%s,%s", username, medicineName, LocalDate.now());
            writer.write(missedDoseData);
            writer.newLine();
            System.out.println("Missed dose recorded: " + medicineName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Refill a medicine when the user chooses to refill
    private static void refillMedicine(String username, String message) {
        String medicineName = message.split(":")[0];  // Assume the message contains the medicine name
        int refillQuantity = 10; // Default quantity to refill

        // Add the medicine back to the user's stock
        List<Medicine> medicines = Medicine.getUserMedicines(username);
        for (Medicine medicine : medicines) {
            if (medicine.getName().equals(medicineName)) {
                Medicine.updateMedicineStock(username, medicineName, medicine.getQuantity() + refillQuantity);
                System.out.println("Refilled " + medicineName + " by " + refillQuantity + " units.");
                return;
            }
        }

        System.out.println("Medicine not found in your inventory.");
    }

    // Remove expired medicine from the user's inventory
    private static void removeExpiredMedicine(String username, String message) {
        String medicineName = message.split(":")[0];  // Assume the message contains the medicine name
        List<Medicine> medicines = Medicine.getUserMedicines(username);

        for (Medicine medicine : medicines) {
            if (medicine.getName().equals(medicineName)) {
                Medicine.removeMedicine(username, medicineName);
                System.out.println("Expired medicine " + medicineName + " has been removed from your stock.");
                return;
            }
        }

        System.out.println("Medicine not found in your inventory.");
    }

    // Add this helper method
    public static void addMedicineTimeNotification(String username, String message) {
        Notification notification = new Notification(
                username,
                message,
                NotificationType.MEDICINE_TIME,
                false
        );
        Notification.addNotification(notification);
    }

    // Method to add refill notifications (for use in ReminderManager or other parts of the system)
    public static void addRefillNotification(String username, String message) {
        // Check if notification already exists
        boolean exists = Notification.getUserNotifications(username).stream()
                .anyMatch(n -> n.getMessage().equals(message) && !n.isProcessed());

        if (!exists) {
            Notification notification = new Notification(
                    username,
                    message,
                    NotificationType.REFILL,
                    false
            );
            Notification.addNotification(notification);
        }
    }

    // Method to add missed dose notifications (for use in ReminderManager or other parts of the system)
    public static void addMissedDoseNotification(String username, String message) {
        Notification missedDoseNotification = new Notification(username, message, NotificationType.MISSED_DOSE, false);
        Notification.addNotification(missedDoseNotification);
    }

    // Method to add expired medicine notifications (for use in ReminderManager or other parts of the system)
    public static void addExpiredMedicineNotification(String username, String message) {
        Notification expiredMedicineNotification = new Notification(username, message, NotificationType.EXPIRED_MEDICINE, false);
        Notification.addNotification(expiredMedicineNotification);
    }
}
