import java.io.Serializable;
import java.time.LocalDateTime;

public class HistoryEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDateTime timestamp;
    private final String actor;
    private final String action;

    public HistoryEntry(String actor, String action) {
        this.timestamp = LocalDateTime.now();
        this.actor = actor;
        this.action = action;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + actor + " -> " + action;
    }
}