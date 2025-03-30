package core;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Medicine {
    private String username;
    private String name;
    private String dosage;
    private int quantity;
    private LocalTime[] times;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate expiryDate;

    private static final String FILE_PATH = "data/medicines.txt";

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

    // Adds a new medicine to the file
    public static boolean addMedicine(Medicine medicine) {
        ensureFileExists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            StringBuilder timesString = new StringBuilder();
            for (LocalTime time : medicine.times) {
                timesString.append(time.toString()).append(";");
            }
            String medicineData = String.join(",", medicine.username, medicine.name, medicine.dosage,
                    String.valueOf(medicine.quantity), timesString.toString(),
                    medicine.startDate.toString(), medicine.endDate.toString(),
                    medicine.expiryDate.toString());
            writer.write(medicineData);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieves all medicines added by a specific user
    public static List<Medicine> getUserMedicines(String username) {
        ensureFileExists();
        List<Medicine> medicines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username)) {
                    String[] timesArray = data[4].split(";");
                    LocalTime[] times = new LocalTime[timesArray.length];
                    for (int i = 0; i < timesArray.length; i++) {
                        times[i] = LocalTime.parse(timesArray[i]);
                    }
                    Medicine medicine = new Medicine(data[0], data[1], data[2], Integer.parseInt(data[3]), times,
                            LocalDate.parse(data[5]), LocalDate.parse(data[6]),
                            LocalDate.parse(data[7]));
                    medicines.add(medicine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return medicines;
    }

    // Updates the stock quantity of a specific medicine
    public static void updateMedicineStock(String username, String medicineName, int newQuantity) {
        ensureFileExists();
        List<Medicine> medicines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username) && data[1].equals(medicineName)) {
                    String[] timesArray = data[4].split(";");
                    LocalTime[] times = new LocalTime[timesArray.length];
                    for (int i = 0; i < timesArray.length; i++) {
                        times[i] = LocalTime.parse(timesArray[i]);
                    }
                    Medicine medicine = new Medicine(data[0], data[1], data[2], newQuantity, times,
                            LocalDate.parse(data[5]), LocalDate.parse(data[6]),
                            LocalDate.parse(data[7]));
                    medicines.add(medicine);
                } else {
                    String[] timesArray = data[4].split(";");
                    LocalTime[] times = new LocalTime[timesArray.length];
                    for (int i = 0; i < timesArray.length; i++) {
                        times[i] = LocalTime.parse(timesArray[i]);
                    }
                    medicines.add(new Medicine(data[0], data[1], data[2], Integer.parseInt(data[3]), times,
                            LocalDate.parse(data[5]), LocalDate.parse(data[6]),
                            LocalDate.parse(data[7])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Overwrite the file with updated data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Medicine med : medicines) {
                StringBuilder timesString = new StringBuilder();
                for (LocalTime time : med.times) {
                    timesString.append(time.toString()).append(";");
                }
                String medicineData = String.join(",", med.username, med.name, med.dosage,
                        String.valueOf(med.quantity), timesString.toString(),
                        med.startDate.toString(), med.endDate.toString(),
                        med.expiryDate.toString());
                writer.write(medicineData);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Removes a specific medicine from the file
    public static void removeMedicine(String username, String medicineName) {
        ensureFileExists();
        List<Medicine> medicines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (!(data[0].equals(username) && data[1].equals(medicineName))) {
                    String[] timesArray = data[4].split(";");
                    LocalTime[] times = new LocalTime[timesArray.length];
                    for (int i = 0; i < timesArray.length; i++) {
                        times[i] = LocalTime.parse(timesArray[i]);
                    }
                    medicines.add(new Medicine(data[0], data[1], data[2], Integer.parseInt(data[3]), times,
                            LocalDate.parse(data[5]), LocalDate.parse(data[6]),
                            LocalDate.parse(data[7])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Overwrite the file with updated data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Medicine med : medicines) {
                StringBuilder timesString = new StringBuilder();
                for (LocalTime time : med.times) {
                    timesString.append(time.toString()).append(";");
                }
                String medicineData = String.join(",", med.username, med.name, med.dosage,
                        String.valueOf(med.quantity), timesString.toString(),
                        med.startDate.toString(), med.endDate.toString(),
                        med.expiryDate.toString());
                writer.write(medicineData);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ensures the medicine file exists
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

    // Getters

    public String getName() {
        return name;
    }

    public String getDosage() {
        return dosage;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public int getQuantity() {
        return quantity;
    }
}
