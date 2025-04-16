package core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DoseHistory {
    private String username;
    private String medicineName;
    private LocalDateTime scheduledTime; // When the medicine was supposed to be taken
    private LocalDateTime takenTime;     // When the medicine was actually taken

    // Constructor with both scheduled and taken times.
    public DoseHistory(String username, String medicineName, LocalDateTime scheduledTime, LocalDateTime takenTime) {
        this.username = username;
        this.medicineName = medicineName;
        this.scheduledTime = scheduledTime;
        this.takenTime = takenTime;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public LocalDateTime getTakenTime() {
        return takenTime;
    }

    // Converts a DoseHistory entry to CSV format: username,medicineName,scheduledTime,takenTime
    public String toCSV() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return username + "," + medicineName + "," + scheduledTime.format(formatter) + "," + takenTime.format(formatter);
    }

    // Parses a CSV line to a DoseHistory object.
    public static DoseHistory fromCSV(String csvLine) {
        String[] values = csvLine.split(",");
        if (values.length == 4) {
            String username = values[0];
            String medicineName = values[1];
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime scheduledTime = LocalDateTime.parse(values[2], formatter);
            LocalDateTime takenTime = LocalDateTime.parse(values[3], formatter);
            return new DoseHistory(username, medicineName, scheduledTime, takenTime);
        }
        return null;
    }
}
