package core;

import java.io.*;
import java.time.LocalTime;
import java.util.*;
import UI.*;

public class ReminderManager {

    // Method to add a reminder for a selected medicine
    public static void addReminder(String username, String medicineName) {
        // Generate the medicine schedule (reminders) automatically
        Reminder.generateMedicineSchedule(username, medicineName);
        System.out.println("Reminders have been generated for " + medicineName);
    }

    // Method to view all reminders for a specific medicine
    public static void viewReminders(String username, String medicineName) {
        List<Reminder> reminders = Reminder.getRemindersByMedicine(username, medicineName);

        // Sort reminders
        reminders.sort((r1, r2) -> {
            int dateCompare = r1.getDate().compareTo(r2.getDate());
            if (dateCompare != 0) return dateCompare;
            return r1.getTime().compareTo(r2.getTime());
        });

        if (reminders.isEmpty()) {
            System.out.println("No reminders found for this medicine.");
            return;
        }

        // Prepare table data
        List<String> headers = List.of("Medicine", "Time", "Date", "Taken?");
        List<List<String>> rows = reminders.stream()
                .map(r -> List.of(
                        r.getMedicineName(),
                        r.getTime().toString(),
                        r.getDate().toString(),
                        r.isTaken() ? "Yes" : "No"
                ))
                .toList();

        // Display using UI tools
        UI.displayReminderTable(headers, rows);
    }

    // Method to get the next dose time for a specific medicine
    public static void viewNextDoseTime(String username, String medicineName) {
        LocalTime nextDoseTime = Reminder.getNextDoseTime(username, medicineName);

        if (nextDoseTime != null) {
            System.out.println("The next dose time for " + medicineName + " is at " + nextDoseTime);
        } else {
            System.out.println("No upcoming doses for " + medicineName);
        }
    }
}
