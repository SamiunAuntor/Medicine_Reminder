package controller;

import core.*;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static String currentUser = null;

    public static void main(String[] args) {
        showLandingPage();
    }

    public static void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    private static void showLandingPage() {
        while (true) {
            clearScreen();
            System.out.println("=== Medication Management System ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice = getIntInput(1, 3);

            switch (choice) {
                case 1 -> showLoginScreen();
                case 2 -> showRegistrationScreen();
                case 3 -> System.exit(0);
            }
        }
    }

    private static void showRegistrationScreen() {
        clearScreen();
        UserManager.registerUser();
        pause(1500);
    }

    private static void showLoginScreen() {
        clearScreen();
        System.out.println("=== Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (User.validateLogin(username, password)) {
            currentUser = username;
            ReminderManager.checkMissedDoses(currentUser);
            ReminderManager.checkDueReminders(currentUser);
            showMainDashboard();
        } else {
            System.out.println("Invalid credentials!");
            pause(1500);
        }
    }

    private static void showMainDashboard() {
        while (true) {
            clearScreen();
            // Check for new notifications before showing dashboard
            ReminderManager.checkDueReminders(currentUser);
            ReminderManager.checkMissedDoses(currentUser);
            System.out.println("\n=== Dashboard - " + currentUser + " ===");
            System.out.println("1. Add Medicine");
            System.out.println("2. View Medicines");
            System.out.println("3. Manage Reminders");
            System.out.println("4. Dose History");
            System.out.println("5. Notifications");
            System.out.println("6. Logout");
            System.out.print("Choose option: ");

            int choice = getIntInput(1, 6);

            switch (choice) {
                case 1 -> MedicineManager.addMedicine();
                case 2 -> MedicineManager.viewMedicineList(currentUser);
                case 3 -> manageReminders();
                case 4 -> DoseHistoryManager.displayDoseHistoryByUser(currentUser);
                case 5 -> NotificationManager.processNotifications(currentUser);
                case 6 -> { currentUser = null; return; }
            }
            pause(3000);
        }
    }


    private static void manageReminders() {
        clearScreen();
        System.out.println("\n=== Reminder Management ===");
        System.out.println("1. Add Reminders for Medicine");
        System.out.println("2. View Reminders");
        System.out.println("3. Next Dose Time");
        System.out.println("4. Back");
        System.out.print("Choose option: ");

        int choice = getIntInput(1, 4);
        scanner.nextLine();  // Consume newline

        if (choice == 4) return;

        System.out.print("Enter medicine name: ");
        String medName = scanner.nextLine();

        switch (choice) {
            case 1 -> ReminderManager.addReminder(currentUser, medName);
            case 2 -> ReminderManager.viewReminders(currentUser, medName);
            case 3 -> ReminderManager.viewNextDoseTime(currentUser, medName);
        }
        pause(3000);
    }

    // Utility methods
    private static int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                if (input >= min && input <= max) return input;
                System.out.print("Invalid input. Try again: ");
            } catch (Exception e) {
                scanner.nextLine();  // Clear invalid input
                System.out.print("Numbers only. Try again: ");
            }
        }
    }

    private static void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}