import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
//run using: javac Main.java Bug.java BugManager.java BugStorage.java Comment.java HistoryEntry.java
// then: java Main
public class Main {
    private static final String FILE_NAME = "bugs.dat";

    private static final String[] TEAM_MEMBERS = {
            "Kasra",
            "Sanjaya",
            "Sava",
            "Saad",
            "Oghosa"
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BugManager manager = BugStorage.loadFromFile(FILE_NAME);

        String currentUser = promptNonEmpty(scanner, "Enter your name: ");
        String currentRole = readRole(scanner);

        boolean running = true;
        while (running) {
            printMenu(currentUser, currentRole);
            int choice = readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    addBug(scanner, manager, currentUser);
                    break;
                case 2:
                    viewAllBugs(manager);
                    break;
                case 3:
                    viewBugDetails(scanner, manager, currentRole);
                    break;
                case 4:
                    searchBug(scanner, manager, currentRole);
                    break;
                case 5:
                    updateBugStatus(scanner, manager, currentUser, currentRole);
                    break;
                case 6:
                    deleteBug(scanner, manager, currentRole);
                    break;
                case 7:
                    archiveBug(scanner, manager, currentUser, currentRole);
                    break;
                case 8:
                    assignBug(scanner, manager, currentUser, currentRole);
                    break;
                case 9:
                    claimBug(scanner, manager, currentUser, currentRole);
                    break;
                case 10:
                    filterBugs(scanner, manager);
                    break;
                case 11:
                    addComment(scanner, manager, currentUser);
                    break;
                case 12:
                    saveData(manager);
                    break;
                case 13:
                    manager = BugStorage.loadFromFile(FILE_NAME);
                    System.out.println("Data reloaded from file.");
                    viewAllBugs(manager);
                    break;
                case 14:
                    currentUser = promptNonEmpty(scanner, "Enter new user name: ");
                    currentRole = readRole(scanner);
                    break;
                case 15:
                    saveData(manager);
                    running = false;
                    System.out.println("Exiting system.");
                    break;
                default:
                    System.out.println("Invalid menu choice.");
            }

            System.out.println();
        }

        scanner.close();
    }

    private static void printMenu(String user, String role) {
        System.out.println("======================================");
        System.out.println("Logged in as: " + user + " (" + role + ")");
        System.out.println("1. Create Bug Report");
        System.out.println("2. View Bug List");
        System.out.println("3. View Bug Details");
        System.out.println("4. Search Bug by ID");
        System.out.println("5. Update Bug Status");
        System.out.println("6. Delete Bug");
        System.out.println("7. Archive Bug");
        System.out.println("8. Assign Bug");
        System.out.println("9. Claim Bug");
        System.out.println("10. Filter Bugs by Status");
        System.out.println("11. Add Comment");
        System.out.println("12. Save Data");
        System.out.println("13. Reload Data");
        System.out.println("14. Switch User");
        System.out.println("15. Exit");
        System.out.println("======================================");
    }

    private static String readRole(Scanner scanner) {
        while (true) {
            System.out.print("Enter role (Reporter / Developer / Lead Developer): ");
            String role = scanner.nextLine().trim();

            if (role.equalsIgnoreCase("Reporter")) return "Reporter";
            if (role.equalsIgnoreCase("Developer")) return "Developer";
            if (role.equalsIgnoreCase("Lead Developer")) return "Lead Developer";

            System.out.println("Invalid role.");
        }
    }

    private static void addBug(Scanner scanner, BugManager manager, String currentUser) {
        System.out.println("--- Create Bug Report ---");

        String title = promptNonEmpty(scanner, "Title: ");
        if (title.length() > Bug.MAX_TITLE_LENGTH) {
            System.out.println("Error: Title must be at most " + Bug.MAX_TITLE_LENGTH + " characters.");
            return;
        }

        String description = promptNonEmpty(scanner, "Description: ");
        if (description.length() > Bug.MAX_DESCRIPTION_LENGTH) {
            System.out.println("Error: Description must be at most " + Bug.MAX_DESCRIPTION_LENGTH + " characters.");
            return;
        }

        String type = chooseOption(scanner,
                "Select bug type:",
                new String[]{"Functional", "Performance", "Usability", "Documentation"});

        String artifactType = chooseOption(scanner,
                "Select artifact type:",
                new String[]{"Source File", "Design Document", "Test File"});

        Bug bug = manager.addBug(title, description, type, artifactType, currentUser, LocalDate.now());

        if (bug == null) {
            System.out.println("Error: Bug could not be created. Check validation rules.");
        } else {
            System.out.println("Bug created successfully.");
            System.out.println("Generated Bug ID: " + bug.getBugID());
            System.out.println("Default Status: Open");
            viewAllBugs(manager);
        }
    }

    private static void viewAllBugs(BugManager manager) {
        System.out.println("--- View Bug List ---");
        List<Bug> bugs = manager.getAllVisibleBugs();

        if (bugs.isEmpty()) {
            System.out.println("No bugs reported yet.");
            return;
        }

        for (Bug bug : bugs) {
            System.out.println(bug.getSummary());
        }
    }

    private static void viewBugDetails(Scanner scanner, BugManager manager, String role) {
        System.out.println("--- View Bug Details ---");
        int bugID = readInt(scanner, "Enter Bug ID: ");
        Bug bug = manager.searchBug(bugID);

        if (bug == null) {
            System.out.println("Bug not found.");
            return;
        }

        boolean includeHistory = BugManager.isAuthorizedToModify(role);
        System.out.println(bug.getDetails(includeHistory));
    }

    private static void searchBug(Scanner scanner, BugManager manager, String role) {
        System.out.println("--- Search Bug ---");
        int bugID = readInt(scanner, "Enter Bug ID to search: ");
        Bug bug = manager.searchBug(bugID);

        if (bug == null) {
            System.out.println("No bug found with that ID.");
            return;
        }

        boolean includeHistory = BugManager.isAuthorizedToModify(role);
        System.out.println("Bug found:");
        System.out.println(bug.getDetails(includeHistory));
    }

    private static void updateBugStatus(Scanner scanner, BugManager manager, String currentUser, String currentRole) {
        System.out.println("--- Update Bug Status ---");
        int bugID = readInt(scanner, "Enter Bug ID: ");

        String newStatus = chooseOption(scanner,
                "Select new status:",
                new String[]{"Open", "In Progress", "Fixed", "Closed"});

        boolean success = manager.updateBugStatus(bugID, newStatus, currentUser, currentRole);

        if (!BugManager.isAuthorizedToModify(currentRole)) {
            System.out.println("Only authorized users can modify bug reports.");
        } else if (!success) {
            System.out.println("Update failed. Check Bug ID or bug state.");
        } else {
            System.out.println("Status updated successfully.");
            viewAllBugs(manager);
        }
    }

    private static void deleteBug(Scanner scanner, BugManager manager, String currentRole) {
        System.out.println("--- Delete Bug ---");
        int bugID = readInt(scanner, "Enter Bug ID to delete: ");

        if (!BugManager.isAuthorizedToModify(currentRole)) {
            System.out.println("Only authorized users can delete bug reports.");
            return;
        }

        System.out.print("Are you sure? Type YES to confirm: ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equals("YES")) {
            System.out.println("Delete cancelled.");
            return;
        }

        boolean success = manager.deleteBug(bugID, currentRole);
        if (success) {
            System.out.println("Bug deleted successfully.");
            viewAllBugs(manager);
        } else {
            System.out.println("Bug not found.");
        }
    }

    private static void archiveBug(Scanner scanner, BugManager manager, String currentUser, String currentRole) {
        System.out.println("--- Archive Bug ---");
        int bugID = readInt(scanner, "Enter Bug ID to archive: ");

        if (!BugManager.isAuthorizedToModify(currentRole)) {
            System.out.println("Only authorized users can archive bug reports.");
            return;
        }

        System.out.print("Are you sure? Type YES to confirm: ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equals("YES")) {
            System.out.println("Archive cancelled.");
            return;
        }

        boolean success = manager.archiveBug(bugID, currentUser, currentRole);
        if (success) {
            System.out.println("Bug archived successfully.");
            viewAllBugs(manager);
        } else {
            System.out.println("Bug not found or already archived.");
        }
    }

    private static void assignBug(Scanner scanner, BugManager manager, String currentUser, String currentRole) {
        System.out.println("--- Assign Bug ---");
        int bugID = readInt(scanner, "Enter Bug ID: ");

        String assignee = chooseOption(scanner, "Select team member to assign:", TEAM_MEMBERS);

        boolean success = manager.assignBug(bugID, assignee, currentUser, currentRole);

        if (!BugManager.isAuthorizedToModify(currentRole)) {
            System.out.println("Only authorized users can assign bugs.");
        } else if (!success) {
            System.out.println("Assignment failed. Bug may not exist, may be archived, or may no longer be Open.");
        } else {
            System.out.println("Bug assigned successfully.");
            viewAllBugs(manager);
        }
    }

    private static void claimBug(Scanner scanner, BugManager manager, String currentUser, String currentRole) {
        System.out.println("--- Claim Bug ---");
        int bugID = readInt(scanner, "Enter Bug ID: ");

        boolean success = manager.claimBug(bugID, currentUser, currentRole);

        if (!BugManager.isAuthorizedToModify(currentRole)) {
            System.out.println("Only authorized users can claim bugs.");
        } else if (!success) {
            System.out.println("Claim failed. Bug may not exist, may be archived, or may no longer be Open.");
        } else {
            System.out.println("Bug claimed successfully by " + currentUser + ".");
            viewAllBugs(manager);
        }
    }

    private static void filterBugs(Scanner scanner, BugManager manager) {
        System.out.println("--- Filter Bugs by Status ---");
        String status = chooseOption(scanner,
                "Select status to filter by:",
                new String[]{"Open", "In Progress", "Fixed", "Closed"});

        System.out.print("Include archived bugs? (yes/no): ");
        String include = scanner.nextLine().trim();
        boolean includeArchived = include.equalsIgnoreCase("yes");

        List<Bug> filtered = manager.filterByStatus(status, includeArchived);

        if (filtered.isEmpty()) {
            System.out.println("No bugs found with status: " + status);
            return;
        }

        for (Bug bug : filtered) {
            System.out.println(bug.getSummary());
        }
    }

    private static void addComment(Scanner scanner, BugManager manager, String currentUser) {
        System.out.println("--- Add Comment ---");
        int bugID = readInt(scanner, "Enter Bug ID: ");
        String comment = promptNonEmpty(scanner, "Enter comment: ");

        boolean success = manager.addCommentToBug(bugID, currentUser, comment);

        if (success) {
            System.out.println("Comment added successfully.");
        } else {
            System.out.println("Failed to add comment. Check Bug ID.");
        }
    }

    private static void saveData(BugManager manager) {
        boolean success = BugStorage.saveToFile(FILE_NAME, manager);
        if (success) {
            System.out.println("Data saved successfully.");
        } else {
            System.out.println("Failed to save data.");
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid numeric Bug ID.");
            }
        }
    }

    private static String promptNonEmpty(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field is required.");
        }
    }

    private static String chooseOption(Scanner scanner, String prompt, String[] options) {
        System.out.println(prompt);
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }

        while (true) {
            int choice = readInt(scanner, "Choose option number: ");
            if (choice >= 1 && choice <= options.length) {
                return options[choice - 1];
            }
            System.out.println("Invalid choice.");
        }
    }
}