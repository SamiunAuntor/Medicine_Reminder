package core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DoseHistory {
    private String username;
    private String medicineName;
    private LocalDateTime doseTime;

    // Constructor
    public DoseHistory(String username, String medicineName, LocalDateTime doseTime) {
        this.username = username;
        this.medicineName = medicineName;
        this.doseTime = doseTime;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public LocalDateTime getDoseTime() {
        return doseTime;
    }

    public void setDoseTime(LocalDateTime doseTime) {
        this.doseTime = doseTime;
    }

    // Method to convert the DoseHistory object to CSV format
    public String toCSV() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return username + "," + medicineName + "," + doseTime.format(formatter);
    }

    // Static method to convert a CSV line to a DoseHistory object
    public static DoseHistory fromCSV(String csvLine) {
        String[] values = csvLine.split(",");
        if (values.length == 3) {
            String username = values[0];
            String medicineName = values[1];
            LocalDateTime doseTime = LocalDateTime.parse(values[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return new DoseHistory(username, medicineName, doseTime);
        }
        return null;
    }
}
