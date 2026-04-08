import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Bug implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;

    private final int bugID;
    private final String title;
    private final String description;
    private final String type;
    private final String artifactType;
    private final String reporter;
    private final LocalDate dateFound;

    private String status;
    private LocalDate dateFixed;
    private boolean archived;
    private String assignedTo;

    private final List<Comment> comments;
    private final List<HistoryEntry> history;

    public Bug(int bugID, String title, String description, String type,
               String artifactType, String reporter, LocalDate dateFound) {
        this.bugID = bugID;
        this.title = title;
        this.description = description;
        this.type = type;
        this.artifactType = artifactType;
        this.reporter = reporter;
        this.dateFound = dateFound;
        this.status = "Open";
        this.dateFixed = null;
        this.archived = false;
        this.assignedTo = "Unassigned";
        this.comments = new ArrayList<>();
        this.history = new ArrayList<>();
        this.history.add(new HistoryEntry(reporter, "Created bug with status Open"));
    }

    public int getBugID() {
        return bugID;
    }

    public String getStatus() {
        return status;
    }

    public boolean isArchived() {
        return archived;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public LocalDate getDateFixed() {
        return dateFixed;
    }

    public List<HistoryEntry> getHistory() {
        return new ArrayList<>(history);
    }

    public static boolean isValidStatus(String status) {
        return status.equals("Open")
                || status.equals("In Progress")
                || status.equals("Fixed")
                || status.equals("Closed")
                || status.equals("Archived");
    }

    public static boolean isValidType(String type) {
        return type.equals("Functional")
                || type.equals("Performance")
                || type.equals("Usability")
                || type.equals("Documentation");
    }

    public static boolean isValidArtifactType(String artifactType) {
        return artifactType.equals("Source File")
                || artifactType.equals("Design Document")
                || artifactType.equals("Test File");
    }

    public boolean updateStatus(String newStatus, String actor) {
        if (!isValidStatus(newStatus)) {
            return false;
        }

        String oldStatus = this.status;
        this.status = newStatus;

        if (newStatus.equals("Fixed")) {
            this.dateFixed = LocalDate.now();
        } else {
            this.dateFixed = null;
        }

        history.add(new HistoryEntry(actor, "Changed status from " + oldStatus + " to " + newStatus));
        return true;
    }

    public boolean assignTo(String assignee, String actor) {
        if (!status.equals("Open")) {
            return false;
        }

        String oldAssignee = this.assignedTo;
        this.assignedTo = assignee;
        history.add(new HistoryEntry(actor, "Assigned bug from '" + oldAssignee + "' to '" + assignee + "'"));
        return true;
    }

    public void archive(String actor) {
        this.archived = true;
        history.add(new HistoryEntry(actor, "Archived bug"));
    }

    public void addComment(String author, String text) {
        comments.add(new Comment(author, text));
        history.add(new HistoryEntry(author, "Added comment"));
    }

    public String getSummary() {
        return String.format(
                "ID: %d | Title: %s | Status: %s | Assigned To: %s | Archived: %s",
                bugID, title, status, assignedTo, archived ? "Yes" : "No"
        );
    }

    public String getDetails(boolean includeHistory) {
        StringBuilder sb = new StringBuilder();

        sb.append("Bug #").append(bugID).append("\n");
        sb.append("Title: ").append(title).append("\n");
        sb.append("Description: ").append(description).append("\n");
        sb.append("Type: ").append(type).append("\n");
        sb.append("Artifact Type: ").append(artifactType).append("\n");
        sb.append("Reporter: ").append(reporter).append("\n");
        sb.append("Date Found: ").append(dateFound).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Date Fixed: ").append(dateFixed == null ? "N/A" : dateFixed).append("\n");
        sb.append("Assigned To: ").append(assignedTo).append("\n");
        sb.append("Archived: ").append(archived ? "Yes" : "No").append("\n");

        sb.append("\nComments:\n");
        if (comments.isEmpty()) {
            sb.append("No comments.\n");
        } else {
            for (Comment c : comments) {
                sb.append("- ").append(c).append("\n");
            }
        }

        if (includeHistory) {
            sb.append("\nEdit History:\n");
            if (history.isEmpty()) {
                sb.append("No history.\n");
            } else {
                for (HistoryEntry h : history) {
                    sb.append("- ").append(h).append("\n");
                }
            }
        }

        return sb.toString();
    }
}