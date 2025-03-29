package core;

import java.io.*;
import java.util.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Medicine {
    private String username;
    private String name;
    private String dosage;
    private int quantity;
    private LocalTime[] times;

    private static final String FILE_PATH = "D:\\SWE\\3rd Semester\\SWE 4302 OOP II Lab (Group A)\\OOP II Project\\Medicine Reminder\\medicines.txt";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public Medicine(String username, String name, String dosage, int quantity, LocalTime[] times) {
        this.username = username;
        this.name = name;
        this.dosage = dosage;
        this.quantity = quantity;
        this.times = times;
    }

    // Method to create the medicines file if it doesn't exist
    private static void createMedicinesFileIfNotExist() {
        File file = new File(FILE_PATH);
        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("Created new file: medicines.txt");
                } else {
                    System.out.println("Failed to create the file: medicines.txt");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add a new medicine to the database
    public static boolean addMedicine(String username, String name, String dosage, int quantity, LocalTime[] times) {
        createMedicinesFileIfNotExist();  // Ensure the file exists

        if (doesMedicineExist(username, name)) {
            return false; // Medicine already exists for the user
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            StringJoiner timeJoiner = new StringJoiner(",");
            for (LocalTime time : times) {
                timeJoiner.add(time.format(TIME_FORMATTER));
            }

            writer.write(username + "," + name + "," + dosage + "," + quantity + "," + timeJoiner);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Method to check if the medicine already exists for a user
    public static boolean doesMedicineExist(String username, String name) {
        createMedicinesFileIfNotExist();  // Ensure the file exists

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(username) && details[1].equalsIgnoreCase(name)) {
                    return true; // Medicine already exists for the user
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to get the list of medicines for a user
    public static List<Medicine> getMedicineList(String username) {
        createMedicinesFileIfNotExist();  // Ensure the file exists

        List<Medicine> medicinesList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(username)) {
                    String name = details[1];
                    String dosage = details[2];
                    int quantity = Integer.parseInt(details[3]);
                    String[] timeStrings = details[4].split(",");
                    LocalTime[] times = Arrays.stream(timeStrings)
                            .map(t -> LocalTime.parse(t, TIME_FORMATTER))
                            .toArray(LocalTime[]::new);

                    medicinesList.add(new Medicine(username, name, dosage, quantity, times));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return medicinesList;
    }
}
