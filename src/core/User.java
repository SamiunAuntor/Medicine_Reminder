package core;

import java.io.*;
import java.util.*;

public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String gender;
    private int age;

    // Constructor
    public User(String username, String password, String firstName, String lastName, String gender, int age) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.age = age;
    }

    // Register a user
    public static boolean registerUser(String username, String password, String firstName, String lastName, String gender, int age) {
        if (doesUsernameExist(username)) {
            return false; // Username already taken
        }

        // Create a new user
        User newUser = new User(username, password, firstName, lastName, gender, age);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(username + "," + password + "," + firstName + "," + lastName + "," + gender + "," + age);
            writer.newLine();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Method for checking if username already exists
    public static boolean doesUsernameExist(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails[0].equals(username)) {
                    return true;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to validate login
    public static boolean validateLogin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if(userDetails[0].equals(username) && userDetails[1].equals(password)) {
                    return true; // Login successful
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // getters

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }
}
