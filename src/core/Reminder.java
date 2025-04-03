package core;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Reminder {
    private String username;
    private String medicineName;
    private LocalTime time;
    private LocalDate date;
    private boolean isTaken;

    private static final String FILE_PATH = "data/reminders.txt";
    private static final String MEDICINE_FILE_PATH = "data/medicines.txt";

    // Constructor
    public Reminder(String username, String medicineName, LocalTime time, LocalDate date, boolean isTaken) {
        this.username = username;
        this.medicineName = medicineName;
        this.time = time;
        this.date = date;
        this.isTaken = isTaken;
    }

    // Adds a new reminder to the file
    public static boolean addReminder(Reminder reminder) {
        ensureFileExists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String reminderData = String.join(",", reminder.username, reminder.medicineName, reminder.time.toString(),
                    reminder.date.toString(), String.valueOf(reminder.isTaken));
            writer.write(reminderData);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieves all reminders for a specific medicine
    public static List<Reminder> getRemindersByMedicine(String username, String medicineName) {
        ensureFileExists();
        List<Reminder> reminders = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username) && data[1].equals(medicineName)) {
                    Reminder reminder = new Reminder(data[0], data[1], LocalTime.parse(data[2]),
                            LocalDate.parse(data[3]), Boolean.parseBoolean(data[4]));
                    reminders.add(reminder);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reminders;
    }

    // Retrieves the next dose date and time for a specific medicine
    public static LocalDateTime getNextDoseDateTime(String username, String medicineName) {
        ensureFileExists();
        List<Reminder> reminders = getRemindersByMedicine(username, medicineName);

        // Sorting reminders by date and time to find the next dose time
        reminders.sort(Comparator.comparing(Reminder::getDate).thenComparing(Reminder::getTime));

        for (Reminder reminder : reminders) {
            if (!reminder.isTaken) {
                return LocalDateTime.of(reminder.getDate(), reminder.getTime());
            }
        }
        return null; // No upcoming dose
    }


    // Generate a schedule based on the medicine's intake times and dates
    // In Reminder.java - Replace only this method
    public static void generateMedicineSchedule(String username, String medicineName) {
        ensureFileExists();
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username) && data[1].equals(medicineName)) {
                    // Corrected: Get times from column index 4 (was previously reading from wrong column)
                    String[] timesArray = data[4].split(";");
                    LocalTime[] times = new LocalTime[timesArray.length];
                    for (int i = 0; i < timesArray.length; i++) {
                        times[i] = LocalTime.parse(timesArray[i]);
                    }

                    LocalDate startDate = LocalDate.parse(data[5]);  // Column 5: startDate
                    LocalDate endDate = LocalDate.parse(data[6]);    // Column 6: endDate

                    // Generate reminders for each time slot
                    for (LocalTime time : times) {
                        LocalDate currentDate = startDate;
                        while (!currentDate.isAfter(endDate)) {
                            Reminder reminder = new Reminder(username, medicineName, time, currentDate, false);
                            addReminder(reminder);
                            currentDate = currentDate.plusDays(1);
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Parses the times field (for example, "08:00,12:00,18:00" for three doses a day)
    private static LocalTime[] parseTimes(String timesData) {
        String[] timesArray = timesData.split(",");
        LocalTime[] times = new LocalTime[timesArray.length];
        for (int i = 0; i < timesArray.length; i++) {
            times[i] = LocalTime.parse(timesArray[i]);
        }
        return times;
    }

    // Add this method to update reminder status
    public static boolean markReminderAsTaken(String username, String medicineName, LocalDate date, LocalTime time) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username)
                        && data[1].equals(medicineName)
                        && data[3].equals(date.toString())
                        && data[2].equals(time.toString())) {
                    lines.add(String.join(",", data[0], data[1], data[2], data[3], "true")); // Mark as taken
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (found) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return found;
    }

    // Ensures the reminder file exists
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

    // Getters for reminder properties
    public String getUsername() {
        return username;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public LocalTime getTime() {
        return time;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isTaken() {
        return isTaken;
    }
}
