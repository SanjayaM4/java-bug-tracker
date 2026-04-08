import java.io.Serializable;
import java.time.LocalDateTime;

public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String author;
    private final String text;
    private final LocalDateTime timestamp;

    public Comment(String author, String text) {
        this.author = author;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + author + ": " + text;
    }
}