import java.awt.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Field {
    private volatile Color color;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Field() {
        this.color = Color.WHITE;
    }

    public void setColor(Color newValue) {
        lock.writeLock().lock();
        try {
            this.color = newValue;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Color getValue() {
        lock.readLock().lock();
        try {
            return this.color;
        } finally {
            lock.readLock().unlock();
        }
    }
}