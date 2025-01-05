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
                tiles[i][j] = new Tile(i, j);
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

    public Color getTileBackgroundColor(Coordinates coordinates) {
        assert lock.isReadLockedByCurrentThread();
        validatePosition(coordinates.y, coordinates.x);
        return tiles[coordinates.y][coordinates.x].getBackgroundColor();
    }

    public List<ActorAbstract> getActors(Coordinates coordinates) {
        assert lock.isReadLockedByCurrentThread();
        validatePosition(coordinates.y, coordinates.x);
        return tiles[coordinates.y][coordinates.x].getActors();
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
        return lock.isRWLockedByCurrentThread();
    }

    public Tile getTile(Coordinates coordinates) {
        assert isRWLockedByCurrentThread();
        validatePosition(coordinates.y, coordinates.x);
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

    private void writeLockTiles(Coordinates coordinates1, Coordinates coordinates2) {
        assert isRWLockedByCurrentThread();
        validatePosition(coordinates1.y, coordinates1.x);
        validatePosition(coordinates2.y, coordinates2.x);
        if (coordinates1.x < coordinates2.x || coordinates1.y < coordinates2.y) {
            getTile(coordinates1).lock.writeLock().lock();
            getTile(coordinates2).lock.writeLock().lock();
        } else {
            getTile(coordinates2).lock.writeLock().lock();
            getTile(coordinates1).lock.writeLock().lock();
        }
    }

    public void moveActor(ActorAbstract actor, Coordinates new_coordinates) {
        assert isRWLockedByCurrentThread();
        Coordinates old_coordinates = actor.getCoordinates();
        // lock the tile that the actor is currently on and the tile that the actor is moving to
        // in pre-specified order to prevent deadlocks
        writeLockTiles(actor.getCoordinates(), new_coordinates);
        // move the actor
        getTile(actor.getCoordinates()).removeActor(actor);
        getTile(new_coordinates).addActor(actor);
        actor.setCoordinates(new_coordinates);
        // unlock the tiles
        getTile(old_coordinates).lock.writeLock().unlock();
        getTile(new_coordinates).lock.writeLock().unlock();
    }

    public Coordinates getRandomCoordinates() {
        return new Coordinates((int) (Math.random() * getCols()), (int) (Math.random() * getRows()));
    }

    public void plantCarrot(Coordinates coordinates) {
        assert lock.isWriteLockedByCurrentThread();
        getTile(coordinates).lock.writeLock().lock();
        getTile(coordinates).plantCarrot();
        getTile(coordinates).lock.writeLock().unlock();
    }

    public void growCarrots() {
        assert lock.isWriteLockedByCurrentThread();
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                getTile(new Coordinates(j, i)).lock.writeLock().lock();
                getTile(new Coordinates(j, i)).tickCarrotGrowth();
                getTile(new Coordinates(j, i)).lock.writeLock().unlock();
            }
        }
    }

    public Rabbit addRabbit() {
        Rabbit rabbit = new Rabbit(this);
        getTile(rabbit.getCoordinates()).addActor(rabbit);
        return rabbit;
    }

    public boolean killRabbit(Rabbit rabbit
//                           , Dog dog // TODO: implement dog
    ) {
        assert isRWLockedByCurrentThread();
        Tile tile = getTile(rabbit.getCoordinates());
        tile.lock.writeLock().lock();
        boolean killed = tile.killRabbitOnTile(rabbit);
        tile.lock.writeLock().unlock();
        return killed;
    }
}