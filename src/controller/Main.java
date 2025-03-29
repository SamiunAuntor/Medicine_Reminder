package controller;

import core.Medicine;
import core.User;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Display the main menu
        while (true) {
            clearScreen();
            System.out.println("=== MAIN MENU ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            System.out.println("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    clearScreen();
                    loginUser(scanner);
                    break;

                case 2:
                    clearScreen();
                    registerUser(scanner);
                    break;

                case 3:
                    clearScreen();
                    System.out.println("Exiting the application. See you soon!");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    clearScreen();
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Method to clear the colsole screen
    private static void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }
        catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    // Register a new user
    private static void registerUser (Scanner scanner) {
        System.out.println("=== REGISTER USER ===");
        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        if (User.doesUsernameExist(username)) {
            System.out.println("Username already exists. Please try another one.");
            return;
        }
        System.out.println("Enter password: ");
        String password = scanner.nextLine();

        System.out.println("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.println("Enter last name: ");
        String lastName = scanner.nextLine();

        System.out.println("Enter gender");
        String gender = scanner.nextLine();

        System.out.println("Enter age");
        int age = scanner.nextInt();

        boolean registered = User.registerUser(username, password, firstName, lastName, gender, age);
        if (registered) {
            System.out.println("User successfully registered.");
        }
        else {
            System.out.println("Registration failed.");
        }
    }

    // Login existing user
    private static void loginUser (Scanner scanner) {
        System.out.println("=== LOGIN USER ===");
        System.out.println("Enter username: ");
        String loginUsername = scanner.nextLine();
        System.out.println("Enter password: ");
        String loginPassword = scanner.nextLine();

        if (User.validateLogin(loginUsername, loginPassword)) {
            System.out.println("Login successful.");
            userDashboard (loginUsername, scanner);
        }
        else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }

    private static void userDashboard (String username, Scanner scanner) {
        while (true) {
            System.out.println("=== USER DASHBOARD ===");
            System.out.println("1. Add Medicine");
            System.out.println("2. View Medicine");
            System.out.println("3. Add Reminder");
            System.out.println("4. View Reminders");
            System.out.println("5 View Dose History");
            System.out.println("6. View Missed Doses");
            System.out.println("7. Check Refill Alerts");
            System.out.println("8. View notifications");
            System.out.println("9. Logout");

            System.out.println("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    clearScreen();
                    System.out.println("=== ADD MEDICINE ===");
                    addMedicine(username, scanner);
                    break;

                case 2:
                    clearScreen();
                    System.out.println("=== MEDICINE LIST ===");

                    break;

                case 3:
                    System.out.println("=== ADD REMINDER ===");
                    break;

                case 4:
                    clearScreen();
                    System.out.println("=== REMINDER LIST ===");
                    break;

                case 5:
                    clearScreen();
                    System.out.println("=== DOSE HISTORY ===");
                    break;

                case 6:
                    clearScreen();
                    System.out.println("=== MISSED DOSES ===");
                    break;

                case 7:
                    clearScreen();
                    System.out.println("=== REFILL ALERTS ===");

                    break;

                case 8:
                    clearScreen();
                    System.out.println("=== NOTIFICATIONS ===");
                    break;

                case 9:
                    clearScreen();
                    System.out.println("LOGGING OUT ...");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");

            }
        }
    }

    private static void addMedicine (String username, Scanner scanner) {
        System.out.println("Enter medicine name : ");
        String medicineName = scanner.nextLine();

        System.out.println("Enter dosage (e.g., 500mg) : ");
        String dosage = scanner.nextLine();

        System.out.println("Enter quantity :");
        int quantity = scanner.nextInt();

        int timesPerDay;

        while (true) {
            System.out.println("Enter number of times you take this medicine per day (1, 2 or 3) : ");
            timesPerDay = scanner.nextInt();
            scanner.nextLine();
            if (timesPerDay >=1 && timesPerDay <= 3) {
                break;
            }
            System.out.println("Invalid input! Please enter 1, 2 or 3.");
        }

        LocalTime[] times = new LocalTime[timesPerDay];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i < timesPerDay; i++) {
            while (true) {
                System.out.println("Enter time for dose " + (i + 1) + "HH:mm, e.g., 08:00 or 14:30 : ");
                String timeInput = scanner.nextLine();

                try {
                    times[i] = LocalTime.parse(timeInput, formatter);
                    break;
                }
                catch (DateTimeParseException e) {
                    System.out.println("Invalid time format! Please enter in HH:mm format.");
                }
            }
        }

        boolean added = Medicine.addMedicine(username, medicineName, dosage, quantity, times);
        if (added) {
            System.out.println("Medicine successfully added. Press enter to return to Dashboard...");
        }
        else {
            System.out.println("Medicine already exists. Please enter to return to Dashboard...");
        }
    }
}
