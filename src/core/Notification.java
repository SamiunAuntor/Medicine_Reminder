package core;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Notification {
    private String username;
    private String message;
    private NotificationType type;
    private boolean isProcessed;

    private static final String FILE_PATH = "data/notifications.txt";

    // Constructor
    public Notification(String username, String message, NotificationType type, boolean isProcessed) {
        this.username = username;
        this.message = message;
        this.type = type;
        this.isProcessed = isProcessed;
    }

    // Adds a notification to the notifications file
    public static boolean addNotification(Notification notification) {
        ensureFileExists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String notificationData = String.join(",", notification.username, notification.message, notification.type.name(), String.valueOf(notification.isProcessed));
            writer.write(notificationData);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieves all notifications for a specific user
    public static List<Notification> getUserNotifications(String username) {
        ensureFileExists();
        List<Notification> notifications = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username)) {
                    Notification notification = new Notification(data[0], data[1], NotificationType.valueOf(data[2]), Boolean.parseBoolean(data[3]));
                    notifications.add(notification);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    // Marks a notification as processed
    public static void markNotificationAsProcessed(String username, String message) {
        ensureFileExists();

        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(Paths.get(FILE_PATH)));
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));

            for (String line : lines) {
                String[] data = line.split(",");
                if (data[0].equals(username) && data[1].equals(message)) {
                    data[3] = "true";  // Set the notification as processed
                }
                writer.write(String.join(",", data));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ensures the notifications file exists
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

    // Getters for notification properties
    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public NotificationType getType() {
        return type;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

}
