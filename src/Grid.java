import java.awt.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Grid {
    private final Field[][] fields;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    public Grid(int rows, int cols) {
        fields = new Field[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                fields[i][j] = new Field();
            }
        }
    }

    public void renderLock() {
        lock.writeLock().lock();
    }

    public void renderUnlock() {
        lock.writeLock().unlock();
    }

    public void simulationLock() {
        lock.readLock().lock();
    }

    public void simulationUnlock() {
        lock.readLock().unlock();
    }

    public void setValue(int row, int col, Color value) {
        validatePosition(row, col);
        fields[row][col].setColor(value);
    }

    public Color getValue(int row, int col) {
        validatePosition(row, col);
        return fields[row][col].getValue();
    }

    private void validatePosition(int row, int col) {
        if (row < 0 || row >= fields.length || col < 0 || col >= fields[0].length) {
            throw new IllegalArgumentException("Invalid position: [" + row + "," + col + "]");
        }
    }

    public int getRows() {
        return fields.length;
    }

    public int getCols() {
        return fields[0].length;
    }
}