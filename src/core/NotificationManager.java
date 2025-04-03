package core;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import UI.*;

import static core.Notification.markNotificationAsProcessed;

public class NotificationManager {

    // A single Scanner instance for the entire class.
    private static final Scanner scanner = new Scanner(System.in);

    // Process and display all notifications for the user
    public static void processNotifications(String username) {
        UI.clearScreen();
        List<Notification> notifications = Notification.getUserNotifications(username);

        String[] notificationsOptions = {"Missed Doses", "Refill Alerts", "Expired Medicines", "Medicine Time Alerts", "Back"};
        UI.printBoxedMenu(notificationsOptions, "NOTIFICATIONS MENU");

        int choice = readIntInput();
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

        NotificationType selectedType = types[choice - 1];
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

        // If the type is MISSED_DOSE, display the missed doses
        if (type == NotificationType.MISSED_DOSE) {
            // Filter out the missed doses notifications (if needed, based on time)
            notifications = notifications.stream()
                    .filter(n -> {
                        LocalDateTime notificationTime = LocalDateTime.parse(n.getMessage().split(" due at ")[1].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        return notificationTime.isBefore(LocalDateTime.now().minusDays(2));
                    })
                    .collect(Collectors.toList());
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
        switch (notification.getType()) {
            case MISSED_DOSE:
                // Now simply show missed dose notifications statically in the missed doses section
                displayNotificationTable(notification.getUsername(), NotificationType.MISSED_DOSE);
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


    private static void handleRefill(Notification notification) {
        System.out.println("Refill Notification: " + notification.getMessage());
        System.out.println("1. Refill Now");
        System.out.println("2. Remind Later");
        System.out.print("Choose option: ");
        int choice = readIntInput();

        if (choice == 1) {
            System.out.print("Enter quantity to add: ");
            int quantity = readIntInput();

            String medicineName = notification.getMessage().replace("Refill needed for: ", "").trim();
            int currentQuantity = Medicine.getUserMedicines(notification.getUsername()).stream()
                    .filter(m -> m.getName().equals(medicineName))
                    .findFirst()
                    .map(Medicine::getQuantity)
                    .orElse(0);
            Medicine.updateMedicineStock(
                    notification.getUsername(),
                    medicineName,
                    currentQuantity + quantity
            );
            System.out.println(quantity + " units added to " + medicineName);
        }
    }

    private static void handleExpiredMedicine(Notification notification) {
        System.out.println("Expired Medicine: " + notification.getMessage());
        System.out.println("1. Remove from Inventory");
        System.out.println("2. Keep Anyway");
        System.out.print("Choose option: ");
        int choice = readIntInput();

        if (choice == 1) {
            removeExpiredMedicine(notification.getUsername(), notification.getMessage());
        }
    }



    // Show refill notifications
    public static void showRefillNotifications(Notification notification) {
        System.out.println("Refill Notification: " + notification.getMessage());
        System.out.println("Do you want to refill now? (Yes/No): ");
        String response = scanner.nextLine().trim();

        if (response.equalsIgnoreCase("Yes")) {
            refillMedicine(notification.getUsername(), notification.getMessage());
        } else {
            System.out.println("You chose to add later.");
        }
    }

    private static void handleMedicineTimeNotification(Notification notification) {
        String[] parts = notification.getMessage().split(" due at ");
        if (parts.length != 2) {
            System.out.println("Invalid notification format");
            return;
        }

        String medicineName = parts[0].trim();
        String dateTime = parts[1].trim();

        try {
            LocalDateTime dueTime = LocalDateTime.parse(
                    dateTime,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            );

            String title = String.format("Did you take '%s' at %s?",
                    medicineName,
                    dueTime.format(DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd")));
            String[] options = { "Mark as taken", "Mark as not taken" };

            UI.printBoxedMenu(options, title);
            int choice = readIntInput();

            if (choice == 1) {
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
                // Change notification type to MISSED_DOSE
                Notification updatedNotification = new Notification(
                        notification.getUsername(),
                        notification.getMessage(),
                        NotificationType.MISSED_DOSE,
                        false
                );
                Notification.addNotification(updatedNotification);
            }
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing notification datetime: " + e.getMessage());
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

    // Record a missed dose in the missed doses file without duplicating entries
    private static void recordMissedDose(String username, String medicineName) {
        String missedDoseFile = "data/missed_doses.txt";
        File file = new File(missedDoseFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating missed doses file: " + e.getMessage());
                return;
            }
        }

        String missedDoseData = String.format("%s,%s,%s", username, medicineName, LocalDate.now());
        // Check for duplicates
        boolean alreadyExists = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(missedDoseFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(missedDoseData)) {
                    alreadyExists = true;
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading missed doses file: " + e.getMessage());
        }

        if (!alreadyExists) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(missedDoseFile, true))) {
                writer.write(missedDoseData);
                writer.newLine();
                System.out.println("Missed dose recorded: " + medicineName);
            } catch (IOException e) {
                System.out.println("Error writing to missed doses file: " + e.getMessage());
            }
        } else {
            System.out.println("Missed dose for " + medicineName + " has already been recorded.");
        }
    }

    // Refill a medicine when the user chooses to refill
    private static void refillMedicine(String username, String message) {
        String medicineName = message.split(":")[0].trim();  // Assume the message contains the medicine name
        int refillQuantity = 10; // Default quantity to refill

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
        String medicineName = message.split(":")[0].trim();  // Assume the message contains the medicine name
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

    // Helper methods to add notifications

    // Duplicate-check version for Medicine Time Notifications
    public static void addMedicineTimeNotification(String username, String message) {
        boolean exists = Notification.getUserNotifications(username).stream()
                .anyMatch(n -> n.getType() == NotificationType.MEDICINE_TIME
                        && n.getMessage().equals(message)
                        && !n.isProcessed());
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

    public static void addRefillNotification(String username, String message) {
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

    public static void addMissedDoseNotification(String username, String message) {
        Notification missedDoseNotification = new Notification(username, message, NotificationType.MISSED_DOSE, false);
        Notification.addNotification(missedDoseNotification);
    }

    // Utility method to safely read an integer input
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
}
