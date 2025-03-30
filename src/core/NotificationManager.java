package core;

import java.io.*;
import java.util.*;
import java.time.LocalDate;


public class NotificationManager {

    // Process and display all notifications for the user
    public static void processNotifications(String username) {
        List<Notification> notifications = Notification.getUserNotifications(username);

        if (notifications.isEmpty()) {
            System.out.println("No new notifications.");
            return;
        }

        System.out.println("Notifications for " + username + ":");
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);
            System.out.println((i + 1) + ". " + notification.getType() + " - " + notification.getMessage() +
                    (notification.isProcessed() ? " [Processed]" : " [Unprocessed]"));
        }

        // Ask the user to process a notification
        System.out.println("\nEnter the number of the notification to process (or 0 to exit): ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        if (choice > 0 && choice <= notifications.size()) {
            Notification selectedNotification = notifications.get(choice - 1);
            if (!selectedNotification.isProcessed()) {
                processNotification(selectedNotification);
            } else {
                System.out.println("This notification has already been processed.");
            }
        } else {
            System.out.println("Exiting.");
        }
    }

    // Process a specific notification
    public static void processNotification(Notification notification) {
        switch (notification.getType()) {
            case MISSED_DOSE:
                showMissedDoses(notification);
                break;
            case REFILL:
                showRefillNotifications(notification);
                break;
            case EXPIRED_MEDICINE:
                showExpiredMedicineNotifications(notification);
                break;
            case MEDICINE_TIME:
                handleMedicineTimeNotification(notification);
                break;
            default:
                System.out.println("Notification type not handled.");
        }
        // Mark the notification as processed after handling it
        Notification.markNotificationAsProcessed(notification.getUsername(), notification.getMessage());
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
    public static void handleMedicineTimeNotification(Notification notification) {
        System.out.println("Medicine Time Notification: " + notification.getMessage());
        // Logic to allow user to mark as taken or skip it (missed dose)
        Scanner scanner = new Scanner(System.in);
        System.out.println("Did you take your medicine? (Yes/No): ");
        String response = scanner.nextLine();

        if (response.equalsIgnoreCase("Yes")) {
            // Update the medicine stock (reduce the quantity)
            updateMedicineStock(notification.getUsername(), notification.getMessage(), -1); // Decrease stock
        } else if (response.equalsIgnoreCase("No")) {
            // Record missed dose
            recordMissedDose(notification.getUsername(), notification.getMessage());
        } else {
            System.out.println("Invalid response. Please answer with 'Yes' or 'No'.");
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

    // Method to add refill notifications (for use in ReminderManager or other parts of the system)
    public static void addRefillNotification(String username, String message) {
        Notification refillNotification = new Notification(username, message, NotificationType.REFILL, false);
        Notification.addNotification(refillNotification);
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
