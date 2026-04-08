import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BugManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Bug> bugs;
    private int nextId;

    public BugManager() {
        bugs = new ArrayList<>();
        nextId = 1;
    }

    public Bug addBug(String title, String description, String type,
                      String artifactType, String reporter, LocalDate dateFound) {
        if (title == null || title.trim().isEmpty()) {
            return null;
        }
        if (description == null || description.trim().isEmpty()) {
            return null;
        }

        title = title.trim();
        description = description.trim();

        if (title.length() > Bug.MAX_TITLE_LENGTH) {
            return null;
        }
        if (description.length() > Bug.MAX_DESCRIPTION_LENGTH) {
            return null;
        }

        if (!Bug.isValidType(type)) {
            return null;
        }

        if (!Bug.isValidArtifactType(artifactType)) {
            return null;
        }

        if (reporter == null || reporter.trim().isEmpty()) {
            reporter = "Unknown";
        }

        Bug bug = new Bug(nextId++, title, description, type, artifactType, reporter.trim(), dateFound);
        bugs.add(bug);
        return bug;
    }

    public List<Bug> getAllVisibleBugs() {
        List<Bug> result = new ArrayList<>();
        for (Bug bug : bugs) {
            if (!bug.isArchived()) {
                result.add(bug);
            }
        }
        return result;
    }

    public Bug searchBug(int bugID) {
        for (Bug bug : bugs) {
            if (bug.getBugID() == bugID) {
                return bug;
            }
        }
        return null;
    }

    public boolean updateBugStatus(int bugID, String newStatus, String actor, String role) {
        if (!isAuthorizedToModify(role)) {
            return false;
        }

        Bug bug = searchBug(bugID);
        if (bug == null || bug.isArchived()) {
            return false;
        }

        return bug.updateStatus(newStatus, actor);
    }

    public boolean deleteBug(int bugID, String role) {
        if (!isAuthorizedToModify(role)) {
            return false;
        }

        for (int i = 0; i < bugs.size(); i++) {
            if (bugs.get(i).getBugID() == bugID) {
                bugs.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean archiveBug(int bugID, String actor, String role) {
        if (!isAuthorizedToModify(role)) {
            return false;
        }

        Bug bug = searchBug(bugID);
        if (bug == null || bug.isArchived()) {
            return false;
        }

        bug.archive(actor);
        return true;
    }

    public boolean assignBug(int bugID, String assignee, String actor, String role) {
        if (!isAuthorizedToModify(role)) {
            return false;
        }

        Bug bug = searchBug(bugID);
        if (bug == null || bug.isArchived()) {
            return false;
        }

        return bug.assignTo(assignee, actor);
    }

    public boolean claimBug(int bugID, String actor, String role) {
        return assignBug(bugID, actor, actor, role);
    }

    public boolean addCommentToBug(int bugID, String author, String commentText) {
        if (commentText == null || commentText.trim().isEmpty()) {
            return false;
        }

        Bug bug = searchBug(bugID);
        if (bug == null) {
            return false;
        }

        bug.addComment(author, commentText.trim());
        return true;
    }

    public List<Bug> filterByStatus(String status, boolean includeArchived) {
        List<Bug> filtered = new ArrayList<>();
        for (Bug bug : bugs) {
            if (!includeArchived && bug.isArchived()) {
                continue;
            }
            if (bug.getStatus().equalsIgnoreCase(status)) {
                filtered.add(bug);
            }
        }
        return filtered;
    }

    public static boolean isAuthorizedToModify(String role) {
        return role.equalsIgnoreCase("Developer") || role.equalsIgnoreCase("Lead Developer");
    }
}