package controller;

import core.*;

import java.sql.SQLOutput;
import java.util.Scanner;
import UI.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    public static String currentUser = null;

    public static void main(String[] args) {
        showLandingPage();
    }



    private static void showLandingPage() {
        while (true) {
            UI.clearScreen();

            String[] inititalLandingPageOptions = {"Login", "Register", "Exit"};
            UI.printBoxedMenu(inititalLandingPageOptions, "MEDICIEN REMINDER SYSTEM");

            int choice = getIntInput(1, 3);

            switch (choice) {
                case 1 -> showLoginScreen();
                case 2 -> showRegistrationScreen();
                case 3 -> System.exit(0);
            }
        }
    }

    private static void showRegistrationScreen() {
        UI.clearScreen();
        UserManager.registerUser();
        UI.waitForEnter();
    }

    private static void showLoginScreen() {
        UI.clearScreen();
        UI.printBoxedTitle("LOGIN TO THE SYSTEM");

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
            System.out.println("\nInvalid credentials!");
            UI.waitForEnter();
        }
    }

    private static void showMainDashboard() {
        while (true) {
            UI.clearScreen();

            ReminderManager.checkDueReminders(currentUser);
            ReminderManager.checkMissedDoses(currentUser);

            String[] dashboardOptions = {"Add Medicine", "View Medicines", "Manage Reminders", "Dose History", "Notifications", "Logout"};
            String title = currentUser + "'s " + "DASHBOARD";
            UI.printBoxedMenu(dashboardOptions, title);


            int choice = getIntInput(1, 6);

            switch (choice) {
                case 1 -> MedicineManager.addMedicine();
                case 2 -> MedicineManager.viewMedicineList(currentUser);
                case 3 -> manageReminders();
                case 4 -> DoseHistoryManager.displayDoseHistoryByUser(currentUser);
                case 5 -> NotificationManager.processNotifications(currentUser);
                case 6 -> { currentUser = null; return; }
            }
            UI.waitForEnter();
        }
    }


    private static void manageReminders() {
        UI.clearScreen();

        MedicineManager.viewMedicineList(currentUser);

        System.out.println("\n");

        String[] reminderManagemenetOptions = {"Add Reminder for a Medicine", "View Reminder for a Medicine", "Next Dose Time for a Medicine", "Back"};
        UI.printBoxedMenu(reminderManagemenetOptions, "REMINDER MANAGEMENT");

        int choice = getIntInput(1, 4);

        if (choice == 4) return;

        System.out.print("\nEnter medicine name: ");
        String medName = scanner.nextLine();

        switch (choice) {
            case 1 -> ReminderManager.addReminder(currentUser, medName);
            case 2 -> ReminderManager.viewReminders(currentUser, medName);
            case 3 -> ReminderManager.viewNextDoseTime(currentUser, medName);
        }
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


}