package core;

import java.io.*;

public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Gender gender; // Enum: MALE, FEMALE, OTHER
    private int age;

    private static final String FILE_PATH = "data/users.txt";

    public User(String username, String password, String firstName, String lastName, Gender gender, int age) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.age = age;
    }

    // Registers a new user (writes to file)
    public static boolean registerUser(String username, String password, String firstName, String lastName, Gender gender, int age) {
        ensureFileExists();

        if (doesUsernameExist(username)) {
            System.out.println("Username already exists!");
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String userData = String.join(",", username, password, firstName, lastName, gender.toString(), String.valueOf(age));
            writer.write(userData);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Validates user login
    public static boolean validateLogin(String username, String password) {
        ensureFileExists();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[0].equals(username) && data[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Checks if a username already exists
    public static boolean doesUsernameExist(String username) {
        ensureFileExists();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 0 && data[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Retrieves a user by username
    public static User getUserByUsername(String username) {
        ensureFileExists();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6 && data[0].equals(username)) {
                    return new User(data[0], data[1], data[2], data[3], Gender.valueOf(data[4]), Integer.parseInt(data[5]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Ensures the user file exists
    private static void ensureFileExists() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
