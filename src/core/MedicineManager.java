package core;

import core.Medicine;
import UI.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class MedicineManager {
    private static Scanner scanner = new Scanner(System.in);

    // Adds a new medicine
    public static void addMedicine() {
        System.out.println("\n--- Add New Medicine ---");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter medicine name: ");
        String name = scanner.nextLine();

        System.out.print("Enter dosage: ");
        String dosage = scanner.nextLine();

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter times per day (separate by space, e.g. 08:00 12:00): ");
        String[] timesInput = scanner.nextLine().split(" ");
        LocalTime[] times = new LocalTime[timesInput.length];
        for (int i = 0; i < timesInput.length; i++) {
            times[i] = LocalTime.parse(timesInput[i]);
        }

        System.out.print("Enter start date (yyyy-MM-dd): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter end date (yyyy-MM-dd): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter expiry date (yyyy-MM-dd): ");
        LocalDate expiryDate = LocalDate.parse(scanner.nextLine());

        Medicine medicine = new Medicine(username, name, dosage, quantity, times, startDate, endDate, expiryDate);
        boolean success = Medicine.addMedicine(medicine);
        if (success) {
            System.out.println("Medicine added successfully!");
        } else {
            System.out.println("Failed to add medicine.");
        }
    }

    // View all medicines for a user
    public static void viewMedicineList(String username) {
        var medicines = Medicine.getUserMedicines(username);

        if (medicines.isEmpty()) {
            System.out.println("No medicines found");
            return;
        }

        List<String> headers = List.of("Name", "Dosage", "Quantity", "Expiry");
        List<List<String>> rows = new ArrayList<>();

        for (Medicine med : medicines) {
            rows.add(List.of(
                    med.getName(),
                    med.getDosage(),
                    String.valueOf(med.getQuantity()),
                    med.getExpiryDate().toString()
            ));
        }

        UI.displayReminderTable(headers, rows);
    }


    // Refill a medicine (updates stock quantity)
    public static void refillMedicine() {
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter medicine name: ");
        String medicineName = scanner.nextLine();

        System.out.print("Enter new quantity: ");
        int newQuantity = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        Medicine.updateMedicineStock(username, medicineName, newQuantity);
        System.out.println("Medicine stock updated successfully!");
    }

    // Remove expired medicines
    public static void removeExpiredMedicine() {
        System.out.print("\nEnter username to remove expired medicines: ");
        String username = scanner.nextLine();

        var medicines = Medicine.getUserMedicines(username);
        for (Medicine medicine : medicines) {
            if (medicine.getExpiryDate().isBefore(LocalDate.now())) {
                Medicine.removeMedicine(username, medicine.getName());
                System.out.println("Removed expired medicine: " + medicine.getName());
            }
        }
    }
}
