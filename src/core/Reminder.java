package core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Reminder {

    private Timer timer = new Timer();
    private List<Medicine> medicines;

    // Constructor no longer takes username, it will fetch medicines later
    public Reminder() {
        this.medicines = new ArrayList<>();
    }

    // Fetch medicines for the given user
    public void fetchMedicines(String username) {
        this.medicines = Medicine.getMedicineList(username);  // Fetch medicines for the specific user
    }

    // Start checking for time, stock-out, and expiry notifications
    public void startReminder(String username) {
        fetchMedicines(username);  // Fetch medicines based on username

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkMedicineTime();
                checkStockForNotification();
                checkExpiryForNotification();
            }
        }, 0, 60000);  // Run every minute
    }

    // Check if it's time to take a medicine
    private void checkMedicineTime() {
        LocalTime currentTime = LocalTime.now();

        for (Medicine medicine : medicines) {
            for (LocalTime time : medicine.getTimes()) {
                if (time.equals(currentTime)) {
                    NotificationManager.addNotification(medicine.getUsername(), medicine.getName(), currentTime, "PENDING");
                }
            }
        }
    }

    // Check for stock-out medicines
    private void checkStockForNotification() {
        for (Medicine medicine : medicines) {
            if (medicine.getQuantity() <= 0 && medicine.getEndDate().isAfter(LocalDate.now())) {
                NotificationManager.addExpiryOrStockOutNotification(medicine.getUsername(), medicine.getName(), "Out of Stock");
            }
        }
    }

    // Check for expired medicines
    private void checkExpiryForNotification() {
        for (Medicine medicine : medicines) {
            if (medicine.getExpiryDate().isBefore(LocalDate.now()) && medicine.getQuantity() > 0) {
                NotificationManager.addExpiryOrStockOutNotification(medicine.getUsername(), medicine.getName(), "EXPIRED");
            }
        }
    }

    // Stop the reminder service
    public void stopReminder() {
        timer.cancel();
    }
}
