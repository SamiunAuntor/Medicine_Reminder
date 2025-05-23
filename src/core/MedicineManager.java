package core;

import core.Medicine;
import UI.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

import static controller.Main.currentUser;

public class MedicineManager {
    private static Scanner scanner = new Scanner(System.in);

    // Adds a new medicine
    public static void addMedicine() {
        UI.clearScreen();
        UI.printBoxedTitle("ADD A NEW MEDICINE");


        String username = currentUser;

        System.out.print("Enter medicine name: ");
        String name = scanner.nextLine();

        System.out.print("Enter dosage (e.g. 50 mg): ");
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

        System.out.print("Enter start date (YYYY-MM-DD): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter end date (YYYY-MM-DD): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter expiry date (YYYY-MM-DD): ");
        LocalDate expiryDate = LocalDate.parse(scanner.nextLine());

        Medicine medicine = new Medicine(username, name, dosage, quantity, times, startDate, endDate, expiryDate);
        boolean success = Medicine.addMedicine(medicine);
        if (success) {
            System.out.println("Medicine added successfully!");
        } else {
            System.out.println("Failed to add medicine.");
        }
    }

    public static void removeMedicine() {
        UI.clearScreen();

        viewMedicineList(currentUser);

        UI.printBoxedTitle("REMOVE MEDICINE");

        String username = currentUser;

        System.out.print("Enter medicine name: ");
        String name = scanner.nextLine();

        boolean success = Medicine.removeMedicine(username, name);
        if (success) {
            UI.printBoxedTitle("Medicine removed successfully!");
        }
        else {
            UI.printBoxedTitle("Failed to remove medicine.");
        }
    }

    // View all medicines for a user
    public static void viewMedicineList(String username) {
        UI.clearScreen();
        String title = "Medicine List of " + username;
        UI.printBoxedTitle(title);

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
}
