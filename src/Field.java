import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Field {
    private final Tile[][] tiles;
    private final ExtendedReentrantReadWriteLock lock = new ExtendedReentrantReadWriteLock(true);

    public Field(int rows, int cols) {
        tiles = new Tile[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tiles[i][j] = new Tile(lock, i, j);
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

    public Color getValue(int row, int col) {
        assert lock.isReadLockedByCurrentThread();
        validatePosition(row, col);
        return tiles[row][col].getValue();
    }

    private void validatePosition(int row, int col) {
        if (row < 0 || row >= tiles.length || col < 0 || col >= tiles[0].length) {
            throw new IllegalArgumentException("Invalid position: [" + row + "," + col + "]");
        }
    }

    public int getRows() {
        return tiles.length;
    }

    public int getCols() {
        return tiles[0].length;
    }

    public boolean isRWLockedByCurrentThread() {
        return lock.isReadLockedByCurrentThread() || lock.isWriteLockedByCurrentThread();
    }

    public Tile getTile(Coordinates coordinates) {
        assert isRWLockedByCurrentThread();
        assert coordinates.x >= 0 && coordinates.x < getCols();
        assert coordinates.y >= 0 && coordinates.y < getRows();
        return tiles[coordinates.y][coordinates.x];
    }

    public List<Coordinates> getNeighbors(Coordinates coordinates) {
        assert isRWLockedByCurrentThread();
        List<Coordinates> neighbors = new LinkedList<Coordinates>();
        if (coordinates.x > 0) {
            neighbors.add(new Coordinates(coordinates.x - 1, coordinates.y));
        }
        if (coordinates.x < getCols() - 1) {
            neighbors.add(new Coordinates(coordinates.x + 1, coordinates.y));
        }
        if (coordinates.y > 0) {
            neighbors.add(new Coordinates(coordinates.x, coordinates.y - 1));
        }
        if (coordinates.y < getRows() - 1) {
            neighbors.add(new Coordinates(coordinates.x, coordinates.y + 1));
        }
        return neighbors;
    }

    private void lockTiles(Coordinates coordinates1, Coordinates coordinates2) {
        assert isRWLockedByCurrentThread();
        if (coordinates1.x < coordinates2.x || coordinates1.y < coordinates2.y) {
            getTile(coordinates1).tile_lock.lock();
            getTile(coordinates2).tile_lock.lock();
        } else {
            getTile(coordinates2).tile_lock.lock();
            getTile(coordinates1).tile_lock.lock();
        }
    }

    public void moveActor(ActorAbstract actor, Coordinates new_coordinates) {
        assert isRWLockedByCurrentThread();
        Coordinates old_coordinates = actor.getCoordinates();
        System.out.println("Moving actor " + actor.getThreadId() + " from " + actor.getCoordinates().x + " " + actor.getCoordinates().y + " to " + new_coordinates.x + " " + new_coordinates.y);
        // lock the tile that the actor is currently on and the tile that the actor is moving to
        // in pre-specified order to prevent deadlocks
        lockTiles(actor.getCoordinates(), new_coordinates);
        // move the actor
        getTile(actor.getCoordinates()).removeActor(actor);
        getTile(new_coordinates).addActor(actor);
        actor.setCoordinates(new_coordinates);
        // unlock the tiles
        getTile(old_coordinates).tile_lock.unlock();
        getTile(new_coordinates).tile_lock.unlock();
    }

    public Coordinates getRandomCoordinates() {
        return new Coordinates((int) (Math.random() * getCols()), (int) (Math.random() * getRows()));
    }

    public Rabbit addRabbit() {
        Rabbit rabbit = new Rabbit(this);
        getTile(rabbit.getCoordinates()).addActor(rabbit);
        return rabbit;
    }
}