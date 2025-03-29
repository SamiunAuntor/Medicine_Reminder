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
    public static void addNotification(String username, String medicineName, LocalTime time, String type) {
        createNotificationsFileIfNotExist(); // Ensure the file exists before adding

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(username + "," + medicineName + "," + time.format(TIME_FORMATTER) + "," + type);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Store notification for expired medicine or stock-out
    public static void addExpiryOrStockOutNotification(String username, String medicineName, String type) {
        createNotificationsFileIfNotExist(); // Ensure the file exists before adding

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(username + "," + medicineName + ",," + type); // No time needed for expiry/stock-out notifications
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
        String[] details = notification.split(",");
        String medicineName = details[1];
        String type = details[3];

        // Show details for the specific medicine first
        showMedicineDetails(medicineName);

        if (type.equals("PENDING")) {
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
        } else if (type.equals("Out of Stock")) {
            // Handle stock-out notification
            handleStockOutNotification(medicineName, scanner);
        } else if (type.equals("EXPIRED")) {
            // Handle expired medicine notification
            handleExpiredMedicineNotification(medicineName, scanner);
        }
    }

    // Show the details of a specific medicine (for both expired and out-of-stock cases)
    private static void showMedicineDetails(String medicineName) {
        // Logic to fetch and display medicine details (e.g., dosage, timings)
        System.out.println("Medicine Details for: " + medicineName);
        // You would fetch the actual details from the medicine database here
        System.out.println("Medicine: " + medicineName);
        System.out.println("Dosage: 2 pills per day");
        System.out.println("Frequency: Twice a day");
        // Add any other medicine details you wish to display
    }

    // Handle stock-out notification and ask user if they want to add quantity now
    private static void handleStockOutNotification(String medicineName, Scanner scanner) {
        System.out.println("Stock-out notification for " + medicineName + ".");
        System.out.println("1. Add Quantity Now\n2. Add Later\n3. Back");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                // Here you would add the logic to instantly add quantity to the stock
                System.out.println("Enter the quantity to add: ");
                int quantity = scanner.nextInt();
                scanner.nextLine();
                updateStock(medicineName, quantity);
                updateNotificationStatus(getStockOutNotification(medicineName), "STOCK UPDATED");
                break;
            case 2:
                // If the user wants to add stock later, we simply return to the menu
                System.out.println("Stock quantity will be added later.");
                break;
            case 3:
                return; // Go back without making any changes
            default:
                System.out.println("Invalid choice. Try again.");
                handleStockOutNotification(medicineName, scanner); // Recurse to ask again
        }
    }

    // Handle expired medicine notification and ask user if they want to add quantity or remove expired medicine
    private static void handleExpiredMedicineNotification(String medicineName, Scanner scanner) {
        System.out.println("Expired medicine notification for " + medicineName + ".");
        System.out.println("1. Add Quantity Now\n2. Remove Expired Medicine\n3. Back");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                // Here you would add the logic to instantly add quantity to the stock
                System.out.println("Enter the quantity to add: ");
                int quantity = scanner.nextInt();
                scanner.nextLine();
                updateStock(medicineName, quantity);
                updateNotificationStatus(getExpiredNotification(medicineName), "STOCK UPDATED");
                break;
            case 2:
                // Logic to remove the expired medicine
                removeExpiredMedicine(medicineName);
                updateNotificationStatus(getExpiredNotification(medicineName), "REMOVED");
                break;
            case 3:
                return; // Go back without making any changes
            default:
                System.out.println("Invalid choice. Try again.");
                handleExpiredMedicineNotification(medicineName, scanner); // Recurse to ask again
        }
    }

    // Update the stock quantity of a medicine (Assume a method to update stock is available)
    private static void updateStock(String medicineName, int quantity) {
        // Logic to update the stock of the medicine goes here
        System.out.println("Updated stock of " + medicineName + " by " + quantity + " units.");
    }

    // Remove the expired medicine from the system (placeholder logic)
    private static void removeExpiredMedicine(String medicineName) {
        System.out.println("Removing expired medicine: " + medicineName);
        // Logic to remove the expired medicine from your system (database or file) goes here
    }

    // Get the stock-out notification string for a given medicine
    private static String getStockOutNotification(String medicineName) {
        return "Stock-out notification for " + medicineName;
    }

    // Get the expired notification string for a given medicine
    private static String getExpiredNotification(String medicineName) {
        return "Expired notification for " + medicineName;
    }

    // Update notification status in file
    private static void updateNotificationStatus(String notification, String status) {
        List<String> allNotifications = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(notification)) {
                    String[] details = line.split(",");
                    details[3] = status; // Update the status to "TAKEN", "SKIPPED", "STOCK UPDATED", or "REMOVED"
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
