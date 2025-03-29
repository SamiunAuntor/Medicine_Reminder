package core;

import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Medicine {
    private String username;
    private String name;
    private String dosage;
    private int quantity;
    private LocalTime[] times;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate expiryDate;

    private static final String FILE_PATH = "D:\\SWE\\3rd Semester\\SWE 4302 OOP II Lab (Group A)\\OOP II Project\\Medicine Reminder\\medicines.txt";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Medicine(String username, String name, String dosage, int quantity, LocalTime[] times, LocalDate startDate, LocalDate endDate, LocalDate expiryDate) {
        this.username = username;
        this.name = name;
        this.dosage = dosage;
        this.quantity = quantity;
        this.times = times;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expiryDate = expiryDate;
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
    public static boolean addMedicine(String username, String name, String dosage, int quantity, LocalTime[] times, LocalDate startDate, LocalDate endDate, LocalDate expiryDate) {
        createMedicinesFileIfNotExist();  // Ensure the file exists

        if (doesMedicineExist(username, name)) {
            return false; // Medicine already exists for the user
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            StringJoiner timeJoiner = new StringJoiner(",");
            for (LocalTime time : times) {
                timeJoiner.add(time.format(TIME_FORMATTER));
            }

            writer.write(username + "," + name + "," + dosage + "," + quantity + "," + timeJoiner + ","
                    + startDate.format(DATE_FORMATTER) + "," + endDate.format(DATE_FORMATTER) + "," + expiryDate.format(DATE_FORMATTER));
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
                    LocalDate startDate = LocalDate.parse(details[5], DATE_FORMATTER);
                    LocalDate endDate = LocalDate.parse(details[6], DATE_FORMATTER);
                    LocalDate expiryDate = LocalDate.parse(details[7], DATE_FORMATTER);

                    medicinesList.add(new Medicine(username, name, dosage, quantity, times, startDate, endDate, expiryDate));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return medicinesList;
    }

    // Method to remove a medicine from the file
    public static boolean removeMedicine(String username, String name) {
        createMedicinesFileIfNotExist();  // Ensure the file exists

        List<Medicine> medicines = getMedicineList(username);  // Get the list of medicines for the user
        boolean removed = false;

        // Filter out the medicine to be removed
        medicines.removeIf(medicine -> medicine.getName().equalsIgnoreCase(name));

        // Rewrite the file with the updated medicine list
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Medicine medicine : medicines) {
                StringJoiner timeJoiner = new StringJoiner(",");
                for (LocalTime time : medicine.getTimes()) {
                    timeJoiner.add(time.format(TIME_FORMATTER));
                }

                writer.write(medicine.getUsername() + "," + medicine.getName() + "," + medicine.getDosage() + "," + medicine.getQuantity() + "," + timeJoiner + ","
                        + medicine.getStartDate().format(DATE_FORMATTER) + "," + medicine.getEndDate().format(DATE_FORMATTER) + "," + medicine.getExpiryDate().format(DATE_FORMATTER));
                writer.newLine();
            }
            removed = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return removed;
    }

    // Getter functions for new fields
    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    // Existing Getter functions
    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getDosage() {
        return dosage;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalTime[] getTimes() {
        return times;
    }
}
