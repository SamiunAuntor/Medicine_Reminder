package core;

import core.User;
import core.Gender;
import java.util.Scanner;

public class UserManager {

    private static Scanner scanner = new Scanner(System.in);

    // Handles user registration
    public static void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter gender (MALE, FEMALE, OTHER): ");
        Gender gender;
        try {
            gender = Gender.valueOf(scanner.nextLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid gender! Defaulting to OTHER.");
            gender = Gender.OTHER;
        }

        System.out.print("Enter age: ");
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid age! Setting to 18.");
            age = 18;
        }

        boolean success = User.registerUser(username, password, firstName, lastName, gender, age);
        if (success) {
            System.out.println("User registered successfully!");
        } else {
            System.out.println("Registration failed. Username may already exist.");
        }
    }

    // Handles user login
    public static void loginUser() {
        System.out.println("\n--- User Login ---");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        boolean isValid = User.validateLogin(username, password);
        if (isValid) {
            System.out.println("Login successful! Welcome, " + username);
        } else {
            System.out.println("Invalid username or password!");
        }
    }
}
