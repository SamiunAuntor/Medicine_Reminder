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

    private static final String FILE_PATH = "medicines.txt";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public Medicine(String username, String name, String dosage, int quantity, LocalTime[] times) {
        this.username = username;
        this.name = name;
        this.dosage = dosage;
        this.quantity = quantity;
        this.times = times;
    }

    // Add a new medicine to the database
    public static boolean addMedicine(String username, String name, String dosage, int quantity, LocalTime[] times) {
        if (doesMedicineExists(username, name)) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            StringJoiner timeJoiner = new StringJoiner(",");
            for (LocalTime time : times) {
                timeJoiner.add(time.format(TIME_FORMATTER));
            }

            writer.write(username + "," + name + "," + dosage + "," + quantity + "," + timeJoiner);
            writer.newLine();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean doesMedicineExists(String username, String name) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(username) && details[1].equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Medicine> getMedicineList(String username) {
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
                    LocalTime[] times = Arrays.stream(timeStrings).map(t -> LocalTime.parse(t, TIME_FORMATTER)).toArray(LocalTime[]::new);

                    medicinesList.add(new Medicine(username, name, dosage, quantity, times));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return medicinesList;
    }
}
