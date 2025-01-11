import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Field extends ThreadAbstract {
    private final Tile[][] tiles;
    private final ExtendedReentrantReadWriteLock lock = new ExtendedReentrantReadWriteLock(true);
    private final static float RABBIT_SPAWN_PROBABILITY = 0.2f;

    public Field(int size) {
        Tile[][] tiles = new Tile[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                tiles[i][j] = new Tile(i, j);
            }
        }
        this.tiles = tiles;
        // start thread
        super();
    }

    @Override
    public void tick() {
        lock.writeLock().lock();
        growCarrots();
        if (Math.random() < RABBIT_SPAWN_PROBABILITY) {
            addRabbit();
        }
        lock.writeLock().unlock();
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
        assert isRWLockedByCurrentThread();
        validatePosition(coordinates.y, coordinates.x);

        Tile tile = getTile(coordinates);
        tile.lock.readLock().lock();
        Color color = tile.getBackgroundColor();
        tile.lock.readLock().unlock();

        return color;
    }

    public List<ActorAbstract> getActors(Coordinates coordinates) {
        assert lock.isRWLockedByCurrentThread();
        validatePosition(coordinates.y, coordinates.x);

        Tile tile = getTile(coordinates);
        tile.lock.readLock().lock();
        List<ActorAbstract> actors = tile.getActors();
        tile.lock.readLock().unlock();

        return actors;
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
        List<Coordinates> neighbors = new LinkedList<>();
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
        assert Math.abs(old_coordinates.x - new_coordinates.x) + Math.abs(old_coordinates.y - new_coordinates.y) == 1;
        // lock the tile that the actor is currently on and the tile that the actor is moving to
        // in pre-specified order to prevent deadlocks
        if (actor instanceof Rabbit) {
            ((Rabbit) actor).rabbit_mutex.unlock();
        } else if (actor instanceof Dog) {
            ((Dog) actor).dog_mutex.unlock();
        }
        getTile(old_coordinates).lock.writeLock().unlock();
        writeLockTiles(actor.getCoordinates(), new_coordinates);
        if (actor instanceof Rabbit) {
            ((Rabbit) actor).rabbit_mutex.lock();
        } else if (actor instanceof Dog) {
            ((Dog) actor).dog_mutex.lock();
        }
        // move the actor
        getTile(actor.getCoordinates()).removeActor(actor);
        getTile(new_coordinates).addActor(actor);
        actor.setCoordinates(new_coordinates);
        // unlock the tile
        getTile(new_coordinates).lock.writeLock().unlock();
    }

    public Coordinates getRandomCoordinates() {
        return new Coordinates((int) (Math.random() * getCols()), (int) (Math.random() * getRows()));
    }

    public void growCarrots() {
        assert lock.isWriteLockedByCurrentThread();
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                Tile tile = getTile(new Coordinates(j, i));
                tile.lock.writeLock().lock();
                tile.tickCarrotGrowth();
                tile.lock.writeLock().unlock();
            }
        }
    }

    public void addRabbit() {
        new Rabbit(this);
    }

    public void killRabbit(Rabbit rabbit, Dog dog) {
        assert isRWLockedByCurrentThread();
        assert rabbit != null;
        Tile tile = getTile(rabbit.getCoordinates());
        assert tile.lock.isWriteLocked();
        assert dog.dog_mutex.isHeldByCurrentThread();
        rabbit.rabbit_mutex.lock();
        tile.killRabbitOnTile(rabbit);
        dog.removeTarget();
        rabbit.rabbit_mutex.unlock();
    }

    public List<Tile> getTilesInViewRange(Coordinates coordinates, int range) {
        assert isRWLockedByCurrentThread();
        List<Tile> tiles = new LinkedList<>();
        for (int i = coordinates.y - range; i <= coordinates.y + range; i++) {
            for (int j = coordinates.x - range; j <= coordinates.x + range; j++) {
                if (i >= 0 && i < getRows() && j >= 0 && j < getCols() && Math.abs(i - coordinates.y) + Math.abs(j - coordinates.x) <= range) {
                    tiles.add(getTile(new Coordinates(j, i)));
                }
            }
        }
        return tiles;
    }
}