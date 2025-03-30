package controller;

import core.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static String loggedInUser = null;

    public static void main(String[] args) {
        showLandingPage();
    }

    private static void showLandingPage() {
        while (true) {
            clearScreen();
            System.out.println("=== MEDICATION MANAGEMENT SYSTEM ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice = getIntInput(1, 3);

            switch(choice) {
                case 1: handleLogin(); break;
                case 2: handleRegistration(); break;
                case 3: System.exit(0);
            }
        }
    }

    private static void handleRegistration() {
        clearScreen();
        System.out.println("=== USER REGISTRATION ===");
        UserManager.registerUser();
        pauseAndReturn();
    }

    private static void handleLogin() {
        clearScreen();
        System.out.println("=== USER LOGIN ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if(User.validateLogin(username, password)) {
            loggedInUser = username;
            showUserDashboard();
        } else {
            System.out.println("Invalid credentials!");
            pauseAndReturn();
        }
    }

    private static void showUserDashboard() {
        while(loggedInUser != null) {
            clearScreen();
            System.out.println("=== USER DASHBOARD ===");
            System.out.println("Logged in as: " + loggedInUser);
            System.out.println("1. Add Medicine");
            System.out.println("2. View Medicines");
            System.out.println("3. Add Reminder");
            System.out.println("4. View Reminders");
            System.out.println("5. View Dose History");
            System.out.println("6. Notifications");
            System.out.println("7. Logout");
            System.out.print("Choose option: ");

            switch(getIntInput(1, 7)) {
                case 1: handleAddMedicine(); break;
                case 2: handleViewMedicines(); break;
                case 3: handleAddReminder(); break;
                case 4: handleViewReminders(); break;
                case 5: handleDoseHistory(); break;
                case 6: handleNotifications(); break;
                case 7: loggedInUser = null; return;
            }
        }
    }

    private static void handleAddMedicine() {
        clearScreen();
        System.out.println("=== ADD MEDICINE ===");

        try {
            System.out.print("Medicine name: ");
            String name = scanner.nextLine();

            System.out.print("Dosage (e.g., 500mg): ");
            String dosage = scanner.nextLine();

            System.out.print("Initial quantity: ");
            int quantity = getIntInput(1, 1000);

            System.out.print("Dose times (space-separated HH:mm): ");
            LocalTime[] times = validateTimesInput();

            LocalDate startDate = getDateInput("Start date (yyyy-MM-dd): ");
            LocalDate endDate = getDateInput("End date (yyyy-MM-dd): ");
            LocalDate expiryDate = getDateInput("Expiry date (yyyy-MM-dd): ");

            Medicine medicine = new Medicine(loggedInUser, name, dosage, quantity,
                    times, startDate, endDate, expiryDate);
            Medicine.addMedicine(medicine);
            System.out.println("Medicine added successfully!");
        } catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        pauseAndReturn();
    }

    private static LocalTime[] validateTimesInput() {
        while(true) {
            System.out.print("Enter dose times (HH:mm HH:mm...): ");
            String input = scanner.nextLine();
            try {
                String[] times = input.split(" ");
                LocalTime[] parsedTimes = new LocalTime[times.length];
                for(int i = 0; i < times.length; i++) {
                    parsedTimes[i] = LocalTime.parse(times[i]);
                }
                return parsedTimes;
            } catch(DateTimeParseException e) {
                System.out.println("Invalid time format! Example: 08:00 12:30 20:15");
            }
        }
    }

    private static void handleViewMedicines() {
        clearScreen();
        System.out.println("=== YOUR MEDICINES ===");
        List<Medicine> medicines = Medicine.getUserMedicines(loggedInUser);

        if(medicines.isEmpty()) {
            System.out.println("No medicines found!");
        } else {
            for(int i = 0; i < medicines.size(); i++) {
                Medicine med = medicines.get(i);
                System.out.printf("%d. %s (%s) - Qty: %d, Expiry: %s\n",
                        i+1, med.getName(), med.getDosage(), med.getQuantity(), med.getExpiryDate());
            }
        }
        pauseAndReturn();
    }

    private static void handleAddReminder() {
        clearScreen();
        System.out.println("=== ADD REMINDER ===");
        List<Medicine> medicines = Medicine.getUserMedicines(loggedInUser);

        if(medicines.isEmpty()) {
            System.out.println("No medicines available!");
            pauseAndReturn();
            return;
        }

        System.out.println("Select a medicine:");
        for(int i = 0; i < medicines.size(); i++) {
            System.out.printf("%d. %s\n", i+1, medicines.get(i).getName());
        }

        int choice = getIntInput(1, medicines.size()) - 1;
        String medName = medicines.get(choice).getName();

        // Handle adding reminder logic
        ReminderManager.addReminder(loggedInUser, medName);
        System.out.println("Reminders generated for " + medName);
        pauseAndReturn();
    }

    private static void handleViewReminders() {
        clearScreen();
        System.out.println("=== VIEW REMINDERS ===");
        List<Medicine> medicines = Medicine.getUserMedicines(loggedInUser);

        if(medicines.isEmpty()) {
            System.out.println("No medicines available!");
            pauseAndReturn();
            return;
        }

        System.out.println("Select a medicine:");
        for(int i = 0; i < medicines.size(); i++) {
            System.out.printf("%d. %s\n", i+1, medicines.get(i).getName());
        }

        int choice = getIntInput(1, medicines.size()) - 1;
        String medName = medicines.get(choice).getName();

        ReminderManager.viewReminders(loggedInUser, medName);
        pauseAndReturn();
    }

    private static void handleDoseHistory() {
        clearScreen();
        System.out.println("=== DOSE HISTORY ===");
        DoseHistoryManager.displayDoseHistoryByUser(loggedInUser);
        pauseAndReturn();
    }

    private static void handleNotifications() {
        Notification.getUserNotifications(loggedInUser);

        while(true) {
            clearScreen();
            System.out.println("=== NOTIFICATIONS ===");
            System.out.println("1. Medicine Time Alerts");
            System.out.println("2. Missed Doses");
            System.out.println("3. Refill Alerts");
            System.out.println("4. Expired Medicines");
            System.out.println("5. Return to Dashboard");
            System.out.print("Choose option: ");

            int choice = getIntInput(1, 5);
            if(choice == 5) break;

            NotificationType type = switch(choice) {
                case 1 -> NotificationType.MEDICINE_TIME;
                case 2 -> NotificationType.MISSED_DOSE;
                case 3 -> NotificationType.REFILL;
                case 4 -> NotificationType.EXPIRED_MEDICINE;
                default -> null;
            };

            processNotificationsByType(type);
        }
    }

    private static void processNotificationsByType(NotificationType type) {
        List<Notification> notifications = Notification.getUserNotifications(loggedInUser)
                .stream()
                .filter(n -> n.getType() == type && !n.isProcessed())
                .toList();

        if(notifications.isEmpty()) {
            System.out.println("No unprocessed notifications found!");
            pauseAndReturn();
            return;
        }

        clearScreen();
        System.out.println("=== " + type + " NOTIFICATIONS ===");
        for(int i = 0; i < notifications.size(); i++) {
            Notification n = notifications.get(i);
            System.out.println((i+1) + ". " + n.getMessage());
        }

        System.out.print("\nEnter notification number to process (0 to cancel): ");
        int choice = getIntInput(0, notifications.size());
        if(choice == 0) return;

        Notification selected = notifications.get(choice-1);
        NotificationManager.processNotification(selected);
        Notification.markNotificationAsProcessed(selected.getUsername(), selected.getMessage());
        pauseAndReturn();
    }

    // Helper methods
    private static LocalDate getDateInput(String prompt) {
        while(true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine());
            } catch(Exception e) {
                System.out.println("Invalid date format! Use yyyy-MM-dd");
            }
        }
    }

    private static void clearScreen() {
        try {
            if(System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch(Exception e) {
            System.out.println("Error clearing screen: " + e.getMessage());
        }
    }

    private static void pauseAndReturn() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static int getIntInput(int min, int max) {
        while(true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if(input >= min && input <= max) return input;
                System.out.print("Invalid input. Enter between " + min + "-" + max + ": ");
            } catch(NumberFormatException e) {
                System.out.print("Invalid number format. Try again: ");
            }
        }
    }
}
