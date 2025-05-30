package core;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import UI.*;

import static controller.Main.currentUser;

public class ReminderManager {

    // Method to add a reminder for a selected medicine
    public static void addReminder(String username, String medicineName) {
        Reminder.generateMedicineSchedule(username, medicineName);
        UI.printBoxedTitle("Reminders have been generated for " + medicineName);
    }

    // Method to view all reminders for a specific medicine
    public static void viewReminders(String username, String medicineName) {
        UI.clearScreen();
        UI.printBoxedTitle("ALL REMINDERS FOR " + medicineName);

        List<Reminder> reminders = Reminder.getRemindersByMedicine(username, medicineName);

        reminders.sort((r1, r2) -> {
            int dateCompare = r1.getDate().compareTo(r2.getDate());
            return dateCompare != 0 ? dateCompare : r1.getTime().compareTo(r2.getTime());
        });

        if (reminders.isEmpty()) {
            System.out.println("No reminders found for this medicine.");
            return;
        }

        List<String> headers = List.of("Medicine", "Time", "Date", "Taken?");
        List<List<String>> rows = new ArrayList<>();
        for (Reminder r : reminders) {
            rows.add(List.of(
                    r.getMedicineName(),
                    r.getTime().toString(),
                    r.getDate().toString(),
                    r.isTaken() ? "Yes" : "No"
            ));
        }

        UI.displayReminderTable(headers, rows);
    }

    // Method to get the next dose date and time for a specific medicine
    public static void viewNextDoseDateTime(String username, String medicineName) {
        UI.printBoxedTitle("NEXT DOSE TIME OF " + medicineName + " FOR " + username);

        LocalDateTime nextDoseDateTime = Reminder.getNextDoseDateTime(username, medicineName);
        String message;

        if (nextDoseDateTime != null) {
            message = "Next dose of " + medicineName + " is on " + nextDoseDateTime.toLocalDate() +
                    " at " + nextDoseDateTime.toLocalTime();
        } else {
            message = "No upcoming doses for " + medicineName;
        }

        UI.printBoxedTitle(message);
    }



    // Check for missed doses (past dates)
    public static void checkMissedDoses(String username) {
        List<Medicine> medicines = Medicine.getUserMedicines(username);
        LocalDate today = LocalDate.now();

        for (Medicine medicine : medicines) {
            for (Reminder reminder : Reminder.getRemindersByMedicine(username, medicine.getName())) {
                if (reminder.getDate().isBefore(today) && !reminder.isTaken()) {
                    String message = String.format("%s missed at %s on %s",
                            medicine.getName(),
                            reminder.getTime(),
                            reminder.getDate());
                    NotificationManager.addMissedDoseNotification(username, message);
                }
            }
        }
    }

    // Check for due reminders (current/past date-times)
    public static void checkDueReminders(String username) {
        List<Medicine> medicines = Medicine.getUserMedicines(username);
        LocalDateTime now = LocalDateTime.now();

        for (Medicine med : medicines) {
            for (Reminder rem : Reminder.getRemindersByMedicine(username, med.getName())) {
                LocalDateTime reminderTime = LocalDateTime.of(rem.getDate(), rem.getTime());
                if (!rem.isTaken() && reminderTime.isBefore(now)) {
                    String message = String.format("%s due at %s",
                            med.getName(),
                            reminderTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    NotificationManager.addMedicineTimeNotification(username, message);
                }
            }
        }
    }
}