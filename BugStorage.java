import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BugStorage {
    public static boolean saveToFile(String fileName, BugManager manager) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(manager);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static BugManager loadFromFile(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            Object obj = in.readObject();
            if (obj instanceof BugManager) {
                return (BugManager) obj;
            }
        } catch (Exception e) {
            return new BugManager();
        }
        return new BugManager();
    }
}