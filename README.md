# 💊 Medicine Reminder

**Medicine Reminder** is a console-based Java application for managing daily medications. It helps users:
- Schedule medicines with multiple daily doses.
- Receive timely console notifications.
- Mark doses as taken or missed.
- Track dose history (scheduled vs. taken times).
- Review missed doses in a static table.

---

## ✅ Features

- **User Management**
  - Register and login with a username/password.
- **Medicine Management**
  - Add, view, and remove medicines.
  - Specify dosage times, quantities, and expiry dates.
- **Reminder Scheduler**
  - Auto-generates reminders between a medicine’s start and end dates.
- **Notification System**
  - Alerts for each scheduled dose.
  - Mark as _taken_ (records actual time) or _not taken_ (moves to missed).
- **Dose History**
  - Displays **Scheduled Time** vs **Taken Time** for each dose.
- **Missed Doses**
  - Static table listing all processed “not taken” reminders past due.
- **File-based Storage**
  - Uses CSV-style text files under `data/` for persistence.

---

## 🛠 Tools and Technologies

- Java
- VS Code
- Git & Github

---

## 🚀 Installation & Run

```bash
# 1. Clone the repository
git clone https://github.com/SamiunAuntor/Medicine_Reminder.git

# 2. Enter directory
cd Medicine_Reminder

# 3. Compile sources
mkdir -p bin
javac -d bin src/controller/*.java src/core/*.java src/UI/*.java

# 4. Run the application
java -cp bin controller.Main
```

---

## 📂 Project Structure

```
Medicine_Reminder/
├── src/
│   ├── controller/
│   │   └── Main.java          # Entry point
│   ├── core/
│   │   ├── User.java          # User model & auth
│   │   ├── Medicine.java      # Medicine model & persistence
│   │   ├── Reminder.java      # Reminder scheduling
│   │   ├── Notification.java  # Notification persistence
│   │   ├── DoseHistory.java   # Dose history model
│   │   └── *Manager.java      # Managers for each domain
│   └── UI/
│       └── UI.java            # Console UI utilities
├── data/                      # CSV text files for persistence
│   ├── users.txt
│   ├── medicines.txt
│   ├── reminders.txt
│   ├── notifications.txt
│   └── dose_history.txt
├── bin/                       # Compiled .class files (after build)
└── README.md                  # This document
```

---

## 🙌 Contribution

Feel free to fork the project, make improvements, and submit a PR.

